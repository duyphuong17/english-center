package com.duyphuong.backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.duyphuong.backend.domain.User;
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

    public List<User> handlefetchAllUser() {
        return this.userRepository.findAll();
    }

    public User handleUpdateUser(User reqUser) {
        User currentUser = this.fetchUserById(reqUser.getId());
        if (currentUser != null) {
            currentUser.setName(reqUser.getName());
            currentUser.setPassword(reqUser.getPassword());
            this.userRepository.save(currentUser);

        }
        return currentUser;

    }

    public User handleGetUserByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }
}
