package com.support.repository;

import com.support.entity.Ticket;
import com.support.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByCreatedBy(User user);
    List<Ticket> findByStatus(Ticket.Status status);
    List<Ticket> findByCreatedByAndStatus(User user, Ticket.Status status);
} 