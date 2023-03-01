package com.todo.project.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class TicketRequest {
    private String title;
    private String description;
    private Date dueDate;


}
