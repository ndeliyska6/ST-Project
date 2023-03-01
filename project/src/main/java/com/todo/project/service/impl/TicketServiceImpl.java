package com.todo.project.service.impl;

import com.todo.project.exceptions.EntityNotFoundException;
import com.todo.project.persistence.model.Ticket;
import com.todo.project.persistence.model.User;
import com.todo.project.persistence.repository.TicketRepository;
import com.todo.project.service.TicketService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;

    public TicketServiceImpl(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Override
    public void createTicket(Ticket ticket) {

        ticketRepository.save(ticket);
    }

    @Override
    public void updateTicket(Ticket ticket) {
        if(ticket.getId() == null){
            throw new EntityNotFoundException("Ticket", ticket.getId());
        }
        ticketRepository.save(ticket);
    }

    @Override
    public void deleteTicket(Ticket ticket){
        ticketRepository.delete(ticket);
    }

    @Override
    public List<Ticket> findTicketsByCreator(User user) {
        return ticketRepository.findTicketByCreator(user);
    }

    @Override
    public Ticket findTicketById(Long ticketId){
        return ticketRepository.findTicketById(ticketId);
    }

    @Override
    public List<Ticket> getAllTickets(){
        return ticketRepository.findAll();
    }

    @Override
    public Ticket findTicketByTitle(String title){
        return ticketRepository.findTicketByTitle(title);
    }

    @Override
    public String checkTitle(String title){
        String message = "";
        if(title == null || title.equals("")){
            message = "Title must not be empty";
        }
        return message;
    }
}
