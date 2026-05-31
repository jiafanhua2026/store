package com.nvzhuang.service;

import com.nvzhuang.entity.User;

import java.util.List;

public interface UserService {
    User login(String username, String password);
    User getUserById(Long id);
    User getUserByUsername(String username);
    User createUser(User user);
    User updateUser(Long id, User user);
    void deleteUser(Long id);
    List<User> getAllUsers();
}