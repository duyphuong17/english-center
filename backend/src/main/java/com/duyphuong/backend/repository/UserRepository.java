package com.duyphuong.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.duyphuong.backend.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    User findByEmail(String email);
}

// save(entity) Thêm hoặc cập nhật
// saveAll(entities) Lưu nhiều bản ghi
// findById(id) Tìm theo ID
// existsById(id) Kiểm tra tồn tại
// findAll() Lấy tất cả
// findAllById(ids) Lấy theo list ID
// count() Đếm số bản ghi
// deleteById(id) Xoá theo ID
// delete(entity) Xoá object
// deleteAll() Xoá hết
// deleteAllById(ids) Xoá nhiều
// fetchUserById tìm user theo id