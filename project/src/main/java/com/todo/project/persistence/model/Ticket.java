package com.todo.project.persistence.model;

import javax.persistence.*;


import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.util.Date;

@Data
@Entity
@Table(name = "tickets")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name="creator")
    private User creator;

    @Column(name = "description")
    private String description;

    @Column(name = "title")
    @NotNull
    private String title;

    @Column(name = "dueDate")
    private Date dueDate;

    public Ticket(){

    }
    public Ticket(User creator, String title, String description, Date dueDate){
        this.creator = creator;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
    }
}
