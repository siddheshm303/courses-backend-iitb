package com.example.coursesBackend.controller;

import com.example.coursesBackend.model.Course;
import com.example.coursesBackend.model.CourseInstance;
import com.example.coursesBackend.repository.CourseInstanceRepository;
import com.example.coursesBackend.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api")
public class CourseController {

    @Autowired
    private CourseRepository courseRepo;

    @Autowired
    private CourseInstanceRepository instanceRepo;

    //POST /api/courses
    @PostMapping("/courses")
    public ResponseEntity<?> createCourse(@RequestBody Course course){

        //Check if course already exists
        if (courseRepo.existsById(course.getCourseId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Course ID already exists: " + course.getCourseId());
        }     
        
        List<Course> validPrereqs = new ArrayList<>();
        for (Course prereq : course.getPrerequisites()){
            Course found = courseRepo.findById(prereq.getCourseId()).orElse(null);
            if (found == null){
                return ResponseEntity.badRequest().body("Invalid Prerequisite: " + prereq.getCourseId());
            }
            validPrereqs.add(found);
        }
        course.setPrerequisites(validPrereqs);
        return ResponseEntity.ok(courseRepo.save(course));
    }

    //GET /api/courses
    @GetMapping("/courses")
    public ResponseEntity<List<Course>> getAllCourses(){

        return ResponseEntity.ok(courseRepo.findAll());
    }

    //GET /api/courses/{id}
    @GetMapping("/courses/{id}")
    public ResponseEntity<?> getCourse(@PathVariable String id) {
        return courseRepo.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Course not found"));
    }


    //DELETE /api/courses/{id}
    @DeleteMapping("courses/{id}")
    public ResponseEntity<?> deleteCourse(@PathVariable String id) {
        Optional<Course> course = courseRepo.findById(id);
        if(course.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found");

        Course courseGet = course.get();

        if(courseRepo.existsByPrerequisitesContaining(courseGet)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Cannot delete course: it is a prerequisite for other course");
        }

        //First delete instances of that course
        List<CourseInstance> instances = instanceRepo.findByCourse(courseGet);
        instanceRepo.deleteAll(instances);

        //Then delete the course
        courseRepo.deleteById(id);
        return ResponseEntity.ok("Course Deleted Successfully");
    }

    //POST /api/instances
    @PostMapping("/instances")
    public ResponseEntity<?> createInstance(@RequestBody CourseInstance instance) {
        Course course = courseRepo.findById(instance.getCourse().getCourseId()).orElse(null);
        if (course == null)
            return ResponseEntity.badRequest()
                    .body("Invalid Course ID: " + instance.getCourse().getCourseId());

        CourseInstance existingInstance = instanceRepo.findByYearAndSemesterAndCourse_CourseId(
                instance.getYear(),instance.getSemester(), instance.getCourse().getCourseId()
        );

        if (existingInstance == null){

        instance.setCourse(course);
        return ResponseEntity.ok(instanceRepo.save(instance));
        }else {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Course instance already exists for this course in the given year and semester.");

        }
    }

    //GET /api/instances/{year}/{sem}
    @GetMapping("/instances/{year}/{sem}")
    public ResponseEntity<List<CourseInstance>> getInstance(@PathVariable int year,@PathVariable int sem) {
        return ResponseEntity.ok(instanceRepo.findByYearAndSemester(year,sem));
    }

    //GET /api/instances/{year}/{sem}/{id}
    @GetMapping("/instances/{year}/{sem}/{id}")
    public ResponseEntity<?> getInstanceDetails(@PathVariable int year,@PathVariable int sem, @PathVariable String id) {
        CourseInstance instance = instanceRepo.findByYearAndSemesterAndCourse_CourseId(year,sem,id);
        if (instance == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Instance not found");

        return ResponseEntity.ok(instance);
    }

    //DELETE /api/instances/{year}/{sem}/{id}
    @DeleteMapping("/instances/{year}/{sem}/{id}")
    public ResponseEntity<?> deleteInstance(@PathVariable int year,@PathVariable int sem,@PathVariable String id){
        CourseInstance instance = instanceRepo.findByYearAndSemesterAndCourse_CourseId(year, sem, id);
        if (instance == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Instance not found.");
        instanceRepo.delete(instance);
        return ResponseEntity.ok("Instance deleted successfully.");
    }

}
