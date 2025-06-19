package com.example.coursesBackend.repository;

import com.example.coursesBackend.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, String> {
    boolean existsByPrerequisitesContaining(Course course);
}