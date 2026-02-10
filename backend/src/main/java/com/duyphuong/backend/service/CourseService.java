package com.duyphuong.backend.service;

import java.lang.StackWalker.Option;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.duyphuong.backend.domain.Course;
import com.duyphuong.backend.domain.response.ResultPaginationDTO;
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

    public ResultPaginationDTO handleFetchAllCourse(Specification<Course> spec, Pageable pageable) {
        Page<Course> pageUser = this.courseRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        // Trang hiện tại (Spring đánh số từ 0 nên +1 cho dễ hiểu)
        mt.setPage(pageable.getPageNumber() + 1);
        // Số phần tử trên mỗi trang
        mt.setPageSize(pageable.getPageSize());

        // Tổng số trang
        mt.setPages(pageUser.getTotalPages());
        // Tổng số khóa học trong database(theo điều kiện filter)
        mt.setTotal(pageUser.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pageUser.getContent());
        return rs;
    }

    public void handleDeleteCourse(long id) {
        this.courseRepository.deleteById(id);
    }

    public Course handleUpdateCourse(Course course) {
        Optional<Course> currentCourse = this.courseRepository.findById(course.getId());
        if (currentCourse.isPresent()) {
            Course courseUpdate = currentCourse.get();
            courseUpdate.setName(course.getName());
            courseUpdate.setDescription(course.getDescription());
            courseUpdate.setPrice(course.getPrice());
            courseUpdate.setThumbnail(course.getThumbnail());
            return this.courseRepository.save(courseUpdate);

        }
        return null;

    }
}
