package com.todo.project.persistence.model;

import javax.persistence.*;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    @Column(name = "id")
    private Long id;

    @Column(name = "firstName")
    @NotNull
    private String firstName;

    @Column(name = "lastName")
    @NotNull
    private String lastName;

    @Column(name = "session_token")
    private String sessionToken;

    @Column(name = "password")
    private String password;

    @Column(name = "email", unique = true)
    @NotNull
    private String email;

    @Column(name = "is_admin")
    @NotNull
    private Boolean isAdmin;

    @OneToMany(mappedBy = "creator",cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<Ticket> tickets;

    public User() {}

    public User(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

}
