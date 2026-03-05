package com.duyphuong.backend.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.duyphuong.backend.domain.User;
import com.duyphuong.backend.domain.response.ResCreateUserDTO;
import com.duyphuong.backend.domain.response.ResUpdateUserDTO;
import com.duyphuong.backend.domain.response.ResUserDTO;
import com.duyphuong.backend.domain.response.ResultPaginationDTO;
import com.duyphuong.backend.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User handleCreateUser(User user) {
        return this.userRepository.save(user);
    }

    public User fetchUserById(long id) {
        Optional<User> userId = this.userRepository.findById(id);
        if (userId.isPresent()) {
            return userId.get();
        }
        return null;
    }

    public void handleDeleteUser(long id) {
        this.userRepository.deleteById(id);
    }

    // public List<User> handlefetchAllUser() {
    // return this.userRepository.findAll();
    // }
    public ResultPaginationDTO handlefetchAllUser(Specification<User> spec, Pageable pageable) {
        Page<User> pageUser = this.userRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageUser.getTotalPages());
        mt.setTotal(pageUser.getTotalElements());

        rs.setMeta(mt);

        // xử lý dữ liệu trả về
        // Lấy danh sách User từ đối tượng phân trang (pageUser)
        // Chuyển List thành Stream để xử lý theo kiểu functional
        // Dùng map() để biến đổi từng phần tử (item) trong danh sách
        // từ đối tượng User -> đối tượng ResUserDTO
        List<ResUserDTO> listUser = pageUser.getContent()
                .stream().map(item -> new ResUserDTO(
                        item.getId(),
                        item.getEmail(),
                        item.getName(),
                        item.getGender(),
                        item.getUpdatedAt(),
                        item.getCreatedAt()))
                // Thu (collect) kết quả Stream lại thành List
                .collect(Collectors.toList());

        rs.setResult(listUser);

        return rs;
    }

    public User handleUpdateUser(User reqUser) {
        User currentUser = this.fetchUserById(reqUser.getId());
        if (currentUser != null) {
            currentUser.setName(reqUser.getName());
            currentUser.setPassword(reqUser.getPassword());
            currentUser.setGender(reqUser.getGender());

            this.userRepository.save(currentUser);

        }
        return currentUser;

    }

    public User handleGetUserByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }

    public boolean handleExistByEmail(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public ResCreateUserDTO converToResCreateUserDTO(User user) {
        ResCreateUserDTO res = new ResCreateUserDTO();

        res.setEmail(user.getEmail());
        res.setGender(user.getGender());
        res.setId(user.getId());
        res.setName(user.getName());
        res.setCreateAt(user.getCreatedAt());
        return res;
    }

    public ResUpdateUserDTO convertToResUpdateUserDTO(User user) {
        ResUpdateUserDTO res = new ResUpdateUserDTO();
        res.setGender(user.getGender());
        res.setId(user.getId());
        res.setName(user.getName());
        res.setUpdateAt(user.getUpdatedAt());
        return res;
    }

    public ResUserDTO convertToResUserDTO(User user) {
        ResUserDTO res = new ResUserDTO();
        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setUpdatedAt(user.getUpdatedAt());
        res.setCreatedAt(user.getCreatedAt());
        res.setGender(user.getGender());
        return res;
    }

    public void updateUserToken(String token, String email) {
        User currentUser = this.handleGetUserByUsername(email);
        if (currentUser != null) {
            currentUser.setRefreshToken(token);
            this.userRepository.save(currentUser);
        }
    }

    public User getUserByRefreshTokenAndEmail(String token, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(token, email);
    }
}
