package com.example.coursesBackend.controller;

import com.example.coursesBackend.model.Course;
import com.example.coursesBackend.repository.CourseInstanceRepository;
import com.example.coursesBackend.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
        if(courseRepo.existsByPrerequisitesContaining(course.get())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Cannot delete course: it is a prerequisite for other course");
        }
        courseRepo.deleteById(id);
        return ResponseEntity.ok("Course Deleted Successfully");
    }

    //POST /api/instances

    //GET /api/instances/{year}/{sem}

    //GET /api/instances/{year}/{sem}/{id}

    //DELETE /api/instances/{year}/{sem}/{id}

}
