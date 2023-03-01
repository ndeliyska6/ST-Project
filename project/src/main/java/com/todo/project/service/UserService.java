package com.todo.project.service;

import com.todo.project.persistence.model.User;

import java.util.List;

public interface UserService {

    void createUser(User user);

    void updateUser(User user);

    void deleteUser(User user);

    List<User> getAllUsers();

    User findUser(Long userId);

    List<User> findUser(String firstName, String lastName);

    User findUser(String sessionToken);

    User findUserByEmail(String email);

    String checkUser(User user);

    User authenticateAndReturnUser(String email, String password);

    String encode(String password);
}
