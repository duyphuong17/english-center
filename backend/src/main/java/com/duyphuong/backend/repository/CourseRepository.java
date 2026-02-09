package com.duyphuong.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.duyphuong.backend.domain.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

}
