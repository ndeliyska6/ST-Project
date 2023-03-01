package com.todo.project.controller;

import com.todo.project.payload.request.TicketRequest;
import com.todo.project.payload.response.TicketResponse;
import com.todo.project.persistence.model.Ticket;
import com.todo.project.persistence.model.User;
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
@RequestMapping("/v1/ticket")
public class TicketController {
    private final TicketService ticketService;
    private final UserService userService;

    public TicketController(TicketService ticketService, UserService userService) {
        this.ticketService = ticketService;
        this.userService = userService;
    }

    @GetMapping("/fetch")
    public ResponseEntity<?> getAllTickets(){
        List<Ticket> tickets = ticketService.getAllTickets();
        List<TicketResponse> result = new ArrayList<>();

        for(Ticket ticket: tickets){
            TicketResponse ticketResponse = new TicketResponse();
            ticketResponse.setCreator(ticket.getCreator());
            ticketResponse.setTitle(ticket.getTitle());
            ticketResponse.setDescription(ticket.getDescription());
            ticketResponse.setDueDate(ticket.getDueDate());

            result.add(ticketResponse);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/find")
    public ResponseEntity<?> findTicketById(Long id){
        Ticket ticket = ticketService.findTicketById(id);
        if(ticket == null){
            return new ResponseEntity<>("Incorrect id.", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(ticket,HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createTicket(@RequestBody TicketRequest ticketRequest,@RequestHeader("session-token") String sessionToken) {
        User creator = userService.findUser(sessionToken);
        if(creator == null){
            return new ResponseEntity<>("Incorrect sessionToken", HttpStatus.BAD_REQUEST);
        }
        if(!ticketService.checkTitle(ticketRequest.getTitle()).equals("")){
            return new ResponseEntity<>(ticketService.checkTitle(ticketRequest.getTitle()), HttpStatus.BAD_REQUEST);
        }
        if(ticketService.findTicketByTitle(ticketRequest.getTitle()) != null){
            return new ResponseEntity<>("Title already in use", HttpStatus.BAD_REQUEST);
        }
        try{
            Ticket ticket = new Ticket(
                    creator,
                    ticketRequest.getTitle(),
                    ticketRequest.getDescription(),
                    ticketRequest.getDueDate()
            );
            ticketService.createTicket(ticket);
            return new ResponseEntity<>("Creation successful", HttpStatus.OK);
        }
        catch (DataIntegrityViolationException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteTicket(@RequestParam Long id, @RequestHeader("session-token") String sessionToken) {
        User creator = userService.findUser(sessionToken);
        if(creator == null){
            return new ResponseEntity<>("Incorrect sessionToken", HttpStatus.BAD_REQUEST);
        }
        Ticket toBeDeleted = ticketService.findTicketById(id);
        if(toBeDeleted == null){
            return new ResponseEntity<>("Incorrect id", HttpStatus.NOT_FOUND);
        }
        if(!toBeDeleted.getCreator().getId().equals(creator.getId())){
            return new ResponseEntity<>("You can delete only your tickets", HttpStatus.BAD_REQUEST);
        }
        ticketService.deleteTicket(toBeDeleted);
        return new ResponseEntity<>("Ticket with id: " + toBeDeleted.getId() + " deleted", HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateTicket(@RequestBody TicketRequest ticketRequest, @RequestHeader("session-token") String sessionToken) {
        User creator = userService.findUser(sessionToken);
        if(creator == null){
            return new ResponseEntity<>("Incorrect sessionToken", HttpStatus.BAD_REQUEST);
        }
        Ticket ticket = ticketService.findTicketByTitle(ticketRequest.getTitle());
        if(!ticket.getCreator().getId().equals(creator.getId())){
            return new ResponseEntity<>("You can update only your tickets", HttpStatus.BAD_REQUEST);
        }
        try{
            ticket.setDescription(ticketRequest.getDescription());
            ticket.setDueDate(ticketRequest.getDueDate());
            ticketService.updateTicket(ticket);
            return new ResponseEntity<>("Ticket is saved", HttpStatus.OK);
        }
        catch (DataIntegrityViolationException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}



// TODO: create controller for each separate model operations we are going to support 
