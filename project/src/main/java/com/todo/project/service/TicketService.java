package com.todo.project.service;

import com.todo.project.persistence.model.Ticket;
import com.todo.project.persistence.model.User;

import java.util.List;

public interface TicketService {

    void createTicket(Ticket ticket);

    void updateTicket(Ticket ticket);

    void deleteTicket(Ticket ticket);

    List<Ticket> findTicketsByCreator(User user);

    Ticket findTicketById(Long ticketId);

    List<Ticket> getAllTickets();

    Ticket findTicketByTitle(String title);

    String checkTitle(String title);
}
