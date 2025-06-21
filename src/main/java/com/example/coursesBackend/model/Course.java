package com.example.coursesBackend.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Course {

    @Id
    @Column(unique = true)
    private String courseId;

    private String title;

    @Column(length = 1000)
    private String description;

    @ManyToMany
    private List<Course> prerequisites;

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Course> getPrerequisites() {
        return prerequisites;
    }

    public void setPrerequisites(List<Course> prerequisites) {
        this.prerequisites = prerequisites;
    }
}
