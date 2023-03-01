package com.todo.project.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.todo.project.exceptions.UserAuthenticationException;
import com.todo.project.payload.request.UserRequest;
import com.todo.project.payload.response.UserResponse;
import com.todo.project.persistence.model.Ticket;
import com.todo.project.persistence.model.User;
import com.todo.project.service.HelperService;
import com.todo.project.service.TicketService;
import com.todo.project.service.UserService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    private final TicketService ticketService;

    public UserController(UserService userService, TicketService ticketService){
        this.userService = userService;
        this.ticketService = ticketService;
    }

    @GetMapping("/fetch")
    public ResponseEntity<?> getAllUsers(){
        List<User> users = userService.getAllUsers();
        List<UserResponse> result = new ArrayList<>();

        for(User user : users){
            UserResponse userResponse = new UserResponse();
            userResponse.setFirstName(user.getFirstName());
            userResponse.setLastName(user.getLastName());
            userResponse.setPassword(user.getPassword());
            userResponse.setEmail(user.getEmail());
            userResponse.setIsAdmin(user.getIsAdmin());

            result.add(userResponse);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/find/id")
    public ResponseEntity<?> findUserById(Long id){
        User user = userService.findUser(id);
        if(user == null){
            return new ResponseEntity<>("Incorrect id.",HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(user,HttpStatus.OK);
    }

    @GetMapping("/find/name")
    public ResponseEntity<?> findUserByFullName(String fName, String lName){
        List<User> result = userService.findUser(fName, lName);
        if(result.isEmpty()){
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/find/token")
    public ResponseEntity<?> getUserBySessionToken(@RequestHeader("session-token") String sessionToken){
        User user = userService.findUser(sessionToken);
        if(user == null){
            return new ResponseEntity<>("Incorrect sessionToken",HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(user,HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User newUser){
        if(!userService.checkUser(newUser).equals("")){
            return new ResponseEntity<>(userService.checkUser(newUser), HttpStatus.BAD_REQUEST);
        }
        if(userService.findUserByEmail(newUser.getEmail()) != null){
            return new ResponseEntity<>("Email already in use",HttpStatus.BAD_REQUEST);
        }
        try{
            newUser.setPassword(userService.encode(newUser.getPassword()));
            newUser.setIsAdmin(false);
            userService.createUser(newUser);
            return new ResponseEntity<>("Register successful",HttpStatus.OK);
        }
        catch (DataIntegrityViolationException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("register/admin")
    public ResponseEntity<?> registerAdmin(@RequestBody User newUser, @RequestHeader("session-token") String sessionToken){
        User admin = userService.findUser(sessionToken);
        if(admin == null){
            return new ResponseEntity<>("Incorrect sessionToken", HttpStatus.BAD_REQUEST);
        }
        if(!admin.getIsAdmin()){
            return new ResponseEntity<>("Only admins can create admin accounts", HttpStatus.BAD_REQUEST);
        }
        if(!userService.checkUser(newUser).equals("")){
            return new ResponseEntity<>(userService.checkUser(newUser), HttpStatus.BAD_REQUEST);
        }
        if(userService.findUserByEmail(newUser.getEmail()) != null){
            return new ResponseEntity<>("Email already in use",HttpStatus.BAD_REQUEST);
        }
        try{
            newUser.setPassword(userService.encode(newUser.getPassword()));
            newUser.setIsAdmin(true);
            userService.createUser(newUser);
            return new ResponseEntity<>("Register successful",HttpStatus.OK);
        }
        catch (DataIntegrityViolationException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody UserRequest userRequest){
        User user = userService.findUserByEmail(userRequest.getEmail());
        if(user.getSessionToken() == null){
            return new ResponseEntity<>("Log in to update user", HttpStatus.BAD_REQUEST);
        }
        try {
            user.setFirstName(userRequest.getFirstName());
            user.setLastName(userRequest.getLastName());
            user.setPassword(userService.encode(userRequest.getPassword()));
            if(!userService.checkUser(user).equals("")){
                return new ResponseEntity<>(userService.checkUser(user), HttpStatus.BAD_REQUEST);
            }
            userService.updateUser(user);
            return new ResponseEntity<>("User is saved", HttpStatus.OK);
        }
        catch (DataIntegrityViolationException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/login")
    public ResponseEntity<?> login(@RequestBody ObjectNode emailAndPasswordInJson){
        String email = emailAndPasswordInJson.get("email").asText();
        String password = emailAndPasswordInJson.get("password").asText();
        User user;
        try{
            user = userService.authenticateAndReturnUser(email, password);
            user.setSessionToken(HelperService.generateNewToken());
            userService.updateUser(user);
            return new ResponseEntity<>(user, HttpStatus.OK);
        }catch (UserAuthenticationException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("session-token") String sessionToken){
        User user = userService.findUser(sessionToken);
        if(user == null){
            return new ResponseEntity<>("Incorrect sessionToken", HttpStatus.BAD_REQUEST);
        }
        user.setSessionToken(null);
        userService.updateUser(user);
        return new ResponseEntity<>("Logout successful", HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(Long id, @RequestHeader("session-token") String sessionToken){
        User admin = userService.findUser(sessionToken);
        if(admin == null){
            return new ResponseEntity<>("Incorrect sessionToken", HttpStatus.BAD_REQUEST);
        }
        if(!admin.getIsAdmin()){
            return new ResponseEntity<>("Only admins can delete accounts", HttpStatus.BAD_REQUEST);
        }
        User toBeDeleted = userService.findUser(id);
        if(toBeDeleted == null){
            return new ResponseEntity<>("Incorrect id", HttpStatus.NOT_FOUND);
        }
        userService.deleteUser(toBeDeleted);
        return new ResponseEntity<>("User with id: " + toBeDeleted.getId() + " deleted", HttpStatus.OK);
    }

    @GetMapping("/tickets")
    public ResponseEntity<?> getUserTickets(@RequestHeader("session-token") String sessionToken){
        User user = userService.findUser(sessionToken);
        if(user == null){
            return new ResponseEntity<>("Incorrect sessionToken.", HttpStatus.BAD_REQUEST);
        }
        List<Ticket> tickets = ticketService.findTicketsByCreator(user);
        if(tickets.isEmpty()){
            return new ResponseEntity<>("No tickets", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(tickets, HttpStatus.OK);
    }
}
