package com.example.coursesBackend.repository;

import com.example.coursesBackend.model.Course;
import com.example.coursesBackend.model.CourseInstance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseInstanceRepository extends JpaRepository<CourseInstance, Long> {
    List<CourseInstance> findByYearAndSemester(int year, int semester);
    CourseInstance findByYearAndSemesterAndCourse_CourseId(int year, int semester, String courseId);
    void deleteByYearAndSemesterAndCourse_CourseId(int year, int semester, String courseId);
    List<CourseInstance> findByCourse(Course course);
}