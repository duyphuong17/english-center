package com.duyphuong.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.duyphuong.backend.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>,
        JpaSpecificationExecutor<User> {
    boolean existsByEmail(String email);

    User findByEmail(String email);

    User findByRefreshTokenAndEmail(String token, String email);
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

// find... Optional<T> hoặc List<T>
// get... T (cẩn thận null)
// exists... boolean
// count... long
// save... T hoặc Iterable<T>
// delete... void

// BẢNG TOÁN TỬ FILTER (RSQL)
// Toán tử ------Ý nghĩa -------Ví dụ trên URL
// == ------------Bằng ---------name=='Java'
// != ------------Khác ----------status!='DELETED'
// ~ -------------Chứa chuỗi (LIKE)------ name~'st'
// !~ ------------- Không chứa chuỗi------- name!~'test'
// > Lớn hơn price>100
// >= Lớn hơn hoặc bằng price>=100
// < Nhỏ hơn price<500
// <= Nhỏ hơn hoặc bằng price<=500
// =in= Nằm trong danh sách level=in=(BEGINNER,ADVANCED)
// =out= Không nằm trong danh sách status=out=(DELETED,DRAFT)
// ==null Giá trị NULL deletedAt==null
// !=null Không NULL deletedAt!=null
// ; AND (và) name~'Boot';active==true
// , OR (hoặc) level==BEGINNER,level==ADVANCED