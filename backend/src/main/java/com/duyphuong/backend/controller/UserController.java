package com.duyphuong.backend.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.duyphuong.backend.domain.User;
import com.duyphuong.backend.domain.response.ResCreateUserDTO;
import com.duyphuong.backend.domain.response.ResUpdateUserDTO;
import com.duyphuong.backend.domain.response.ResUserDTO;
import com.duyphuong.backend.domain.response.ResultPaginationDTO;
import com.duyphuong.backend.service.UserService;
import com.duyphuong.backend.util.annotation.ApiMessage;
import com.duyphuong.backend.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;

@RestController
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/users")
    public ResponseEntity<ResCreateUserDTO> createNewUser(@RequestBody User createUser)
            throws IdInvalidException {
        boolean emaiUser = this.userService.handleExistByEmail(createUser.getEmail());
        if (emaiUser) {
            throw new IdInvalidException("email " + createUser.getEmail() + " đã tồn tại");
        }
        // hash mật khẩu
        String hashPassword = this.passwordEncoder.encode(createUser.getPassword());
        createUser.setPassword(hashPassword);
        User newUser = this.userService.handleCreateUser(createUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.converToResCreateUserDTO(newUser));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ResUserDTO> getUserById(@PathVariable("id") long id) throws IdInvalidException {
        User idUser = this.userService.fetchUserById(id);
        if (idUser == null) {
            throw new IdInvalidException("user với id " + id + " không tồn tại");
        }
        User fetchUser = this.userService.fetchUserById(id);
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.convertToResUserDTO(fetchUser));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") long id)
            throws IdInvalidException {
        User userId = this.userService.fetchUserById(id);
        if (userId == null) {
            throw new IdInvalidException("id truyền lên không tồn tại");
        }
        this.userService.handleDeleteUser(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @GetMapping("/users")
    @ApiMessage("fetch all users")
    public ResponseEntity<ResultPaginationDTO> getAllUser(@Filter Specification<User> spec,
            Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.handlefetchAllUser(spec, pageable));
    }

    @PutMapping("/users")
    public ResponseEntity<ResUpdateUserDTO> updateUser(@RequestBody User user) throws IdInvalidException {

        User idUser = this.userService.fetchUserById(user.getId());
        if (idUser == null) {
            throw new IdInvalidException("user với id " + user.getId() + " không tồn tại");
        }

        User userUpdate = this.userService.handleUpdateUser(user);
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.convertToResUpdateUserDTO(userUpdate));
    }
}
