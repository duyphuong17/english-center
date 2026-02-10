package com.duyphuong.backend.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.duyphuong.backend.domain.Course;
import com.duyphuong.backend.domain.response.ResultPaginationDTO;
import com.duyphuong.backend.service.CourseService;
import com.duyphuong.backend.util.annotation.ApiMessage;
import com.turkraft.springfilter.boot.Filter;

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

    @GetMapping("/courses")
    @ApiMessage("fetch all course")
    public ResponseEntity<ResultPaginationDTO> getAllCourse(@Filter Specification<Course> spec,
            Pageable pageable) {
        // Pageable pageable(dùng phân trang): lấy các giá trị page(hiện tại đang đứng ở
        // trang mấy),size(số phần tử mở mỗi trang),sort(sắp sếp
        // vd:sort=name,asc),page,size,sort là các tên mặc định url truyên lên phải có
        // tên giống như này thì mới lấy được giá trị
        // @Filter Specification<Course> spec(dùng để lọc): chỉ lấy được các giá trị
        // trong URL mà CÓ TÊN THUỘC TÍNH GIỐNG với field trong entity
        return ResponseEntity.status(HttpStatus.OK).body(this.courseService.handleFetchAllCourse(spec, pageable));
    }

    @DeleteMapping("/courses/{id}")
    public ResponseEntity<Void> deletecourse(@PathVariable("id") long id) {
        this.courseService.handleDeleteCourse(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @PutMapping("/courses")
    public ResponseEntity<Course> updateCourse(@RequestBody Course course) {
        return ResponseEntity.status(HttpStatus.OK).body(this.courseService.handleUpdateCourse(course));
    }
}
