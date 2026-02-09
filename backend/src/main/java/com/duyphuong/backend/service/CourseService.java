package com.duyphuong.backend.service;

import org.springframework.stereotype.Service;

import com.duyphuong.backend.domain.Course;
import com.duyphuong.backend.repository.CourseRepository;

@Service
public class CourseService {
    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public Course handleCreateCourse(Course course) {
        return this.courseRepository.save(course);
    }
}
