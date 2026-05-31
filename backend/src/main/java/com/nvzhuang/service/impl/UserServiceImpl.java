package com.nvzhuang.service.impl;

import com.nvzhuang.entity.User;
import com.nvzhuang.repository.UserRepository;
import com.nvzhuang.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public User login(String username, String password) {
        return userRepository.findByUsernameAndPassword(username, password).orElse(null);
    }
    
    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
    
    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
    
    @Override
    @Transactional
    public User createUser(User user) {
        return userRepository.save(user);
    }
    
    @Override
    @Transactional
    public User updateUser(Long id, User user) {
        Optional<User> existing = userRepository.findById(id);
        if (existing.isPresent()) {
            User u = existing.get();
            u.setUsername(user.getUsername());
            u.setPassword(user.getPassword());
            u.setName(user.getName());
            u.setRole(user.getRole());
            u.setStatus(user.getStatus());
            return userRepository.save(u);
        }
        return null;
    }
    
    @Override
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}