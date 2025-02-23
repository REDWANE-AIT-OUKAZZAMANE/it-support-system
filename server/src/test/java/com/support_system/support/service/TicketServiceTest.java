package com.support_system.support.service;

import com.support.dto.CreateTicketRequest;
import com.support.dto.TicketDTO;
import com.support.entity.Ticket;
import com.support.entity.User;
import com.support.repository.AuditLogRepository;
import com.support.repository.TicketRepository;
import com.support.repository.UserRepository;
import com.support.service.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private TicketService ticketService;

    private User testUser;
    private Ticket testTicket;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setRole(User.Role.EMPLOYEE);

        testTicket = new Ticket();
        testTicket.setId(1L);
        testTicket.setTitle("Test Ticket");
        testTicket.setDescription("Test Description");
        testTicket.setStatus(Ticket.Status.NEW);
        testTicket.setCreatedBy(testUser);
    }

    private void setupSecurityContext() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void createTicket_Success() {
        // Arrange
        setupSecurityContext();
        CreateTicketRequest request = new CreateTicketRequest();
        request.setTitle("Test Ticket");
        request.setDescription("Test Description");
        request.setPriority(Ticket.Priority.MEDIUM);
        request.setCategory(Ticket.Category.SOFTWARE);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(testTicket);

        // Act
        TicketDTO result = ticketService.createTicket(request);

        // Assert
        assertNotNull(result);
        assertEquals("Test Ticket", result.getTitle());
        assertEquals("testuser", result.getCreatedByUsername());
        verify(auditLogRepository, times(1)).save(any());
    }

    @Test
    void updateTicketStatus_Success() {
        // Arrange
        setupSecurityContext();
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(testTicket));
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(testTicket);

        // Act
        TicketDTO result = ticketService.updateTicketStatus(1L, Ticket.Status.IN_PROGRESS);

        // Assert
        assertNotNull(result);
        assertEquals(Ticket.Status.IN_PROGRESS, result.getStatus());
        verify(auditLogRepository, times(1)).save(any());
    }

    @Test
    void getTicketsForCurrentUser_AsRegularUser() {
        // Arrange
        setupSecurityContext();
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(ticketRepository.findByCreatedBy(testUser)).thenReturn(Arrays.asList(testTicket));

        // Act
        List<TicketDTO> results = ticketService.getTicketsForCurrentUser();

        // Assert
        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals("Test Ticket", results.get(0).getTitle());
    }

    @Test
    void getTicketsForCurrentUser_AsITSupport() {
        // Arrange
        setupSecurityContext();
        testUser.setRole(User.Role.IT_SUPPORT);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(ticketRepository.findAll()).thenReturn(Arrays.asList(testTicket));

        // Act
        List<TicketDTO> results = ticketService.getTicketsForCurrentUser();

        // Assert
        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals("Test Ticket", results.get(0).getTitle());
    }

    @Test
    void searchTickets_ByTicketId() {
        // Arrange
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(testTicket));

        // Act
        List<TicketDTO> results = ticketService.searchTickets(1L, null);

        // Assert
        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals("Test Ticket", results.get(0).getTitle());
    }

    @Test
    void searchTickets_ByStatus() {
        // Arrange
        when(ticketRepository.findByStatus(Ticket.Status.NEW)).thenReturn(Arrays.asList(testTicket));

        // Act
        List<TicketDTO> results = ticketService.searchTickets(null, Ticket.Status.NEW);

        // Assert
        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals("Test Ticket", results.get(0).getTitle());
    }
} 