package com.todo.project.service.impl;

import com.todo.project.exceptions.EntityNotFoundException;
import com.todo.project.exceptions.UserAuthenticationException;
import com.todo.project.persistence.model.User;
import com.todo.project.persistence.repository.UserRepository;
import com.todo.project.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void createUser(User user){
        userRepository.save(user);
    }

    @Override
    public void updateUser(User user){
        if(user.getId() == null){
            throw new EntityNotFoundException("User", user.getId());
        }
        userRepository.save(user);
    }

    @Override
    public void deleteUser(User user){
        userRepository.delete(user);
    }

    @Override
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    @Override
    public User findUser(Long userId){
        return userRepository.findUserById(userId);
    }

    @Override
    public List<User> findUser(String firstName, String lastName){
        return userRepository.findUserByFirstNameAndLastName(firstName, lastName);
    }

    @Override
    public User findUser(String sessionToken){
        return userRepository.findUserBySessionToken(sessionToken);
    }

    @Override
    public User findUserByEmail(String email){
        return userRepository.findUserByEmail(email);
    }

    @Override
    public String checkUser(User user){
        String message = "";
        if(isEmptyOrNull(user.getFirstName())){
            message = "First Name must not be empty.";
        }else if(isEmptyOrNull(user.getLastName())){
            message = "Last Name must not be empty.";
        }else if(isEmptyOrNull(user.getEmail())){
            message = "Email must not be empty.";
        }else if(isEmptyOrNull(user.getPassword())){
            message = "Password must not be empty.";
        }
        return message;
    }

    public boolean isEmptyOrNull(String string){
        if(string == null || string.equals("")){
            return true;
        }
        return false;
    }

    @Override
    public User authenticateAndReturnUser(String email, String password){
        User user = userRepository.findUserByEmail(email);
        if(user == null){
            throw new UserAuthenticationException("Wrong email or password.");
        }
        if(encoder.matches(password, user.getPassword())){
            return user;
        }else{
            throw new UserAuthenticationException("Wrong email or password.");
        }
    }

    @Override
    public String encode(String password){
        return encoder.encode(password);
    }
}
