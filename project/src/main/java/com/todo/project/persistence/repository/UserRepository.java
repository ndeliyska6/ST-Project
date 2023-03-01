package com.todo.project.persistence.repository;

import com.todo.project.persistence.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface UserRepository extends JpaRepository<User, Long> {

    User findUserByEmail(String email);

    User findUserById(Long id);

    List<User> findUserByFirstNameAndLastName(String firstName, String lastName);

    User findUserBySessionToken(String sessionToken);
}
