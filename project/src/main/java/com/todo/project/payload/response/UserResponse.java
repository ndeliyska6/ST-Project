package com.todo.project.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponse {
    private String firstName;
    private String lastName;
    private String password;
    private String email;
    private Boolean isAdmin;

    public UserResponse() {
    }

}
