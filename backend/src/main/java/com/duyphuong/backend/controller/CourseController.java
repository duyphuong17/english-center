package com.duyphuong.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.duyphuong.backend.domain.Course;
import com.duyphuong.backend.service.CourseService;

@RestController
public class CourseController {
    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping("/courses")
    public ResponseEntity<Course> createCourse(@RequestBody Course newCourse) {
        this.courseService.handleCreateCourse(newCourse);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCourse);
    }
}
