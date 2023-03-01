package com.todo.project.persistence.repository;

import com.todo.project.persistence.model.Ticket;
import com.todo.project.persistence.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long>{

    Ticket findTicketById(Long ticketId);

    List<Ticket> findTicketByCreator(User user);

    Ticket findTicketByTitle(String title);
}


// TODO: create the other needed repository for each model we have