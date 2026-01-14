package com.carlolobitana.helpdesk.service.impl;

import com.carlolobitana.helpdesk.dto.TicketRequestDTO;
import com.carlolobitana.helpdesk.dto.TicketResponseDTO;
import com.carlolobitana.helpdesk.dto.TicketSearchDTO;
import com.carlolobitana.helpdesk.enums.TicketStatus;
import com.carlolobitana.helpdesk.exception.ResourceNotFoundException;
import com.carlolobitana.helpdesk.model.Employee;
import com.carlolobitana.helpdesk.model.FullName;
import com.carlolobitana.helpdesk.model.Ticket;
import com.carlolobitana.helpdesk.repository.EmployeeRepository;
import com.carlolobitana.helpdesk.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceImplTest {

    @Mock private TicketRepository ticketRepository;
    @Mock private EmployeeRepository employeeRepository;

    @InjectMocks private TicketServiceImpl ticketService;

    private Employee sampleEmployee;
    private Ticket sampleTicket;
    private final String TICKET_NO = "ABC12345";

    @BeforeEach
    void setUp() {
        sampleEmployee = new Employee();
        sampleEmployee.setId(1L);
        sampleEmployee.setName(new FullName("John", "M", "Doe"));

        sampleTicket = new Ticket();
        sampleTicket.setTicketNumber(TICKET_NO);
        sampleTicket.setTitle("System Bug");
        sampleTicket.setCreatedBy(sampleEmployee);
        sampleTicket.setStatus(TicketStatus.FILED);
    }

    // --- fileTicket Coverage ---

    @Test
    void fileTicket_Success() {
        TicketRequestDTO dto = new TicketRequestDTO();
        dto.setCreatorId(1L);
        dto.setTitle("New Ticket");

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(sampleEmployee));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(i -> i.getArguments()[0]);

        TicketResponseDTO result = ticketService.fileTicket(dto);

        assertNotNull(result.getTicketNumber());
        assertEquals(TicketStatus.FILED, result.getStatus());
        assertEquals("John Doe", result.getCreatedByName());
    }

    @Test
    void fileTicket_EmployeeNotFound_ThrowsException() {
        // 1. Arrange: Create a DTO and give it an ID
        TicketRequestDTO dto = new TicketRequestDTO();
        dto.setCreatorId(999L); // Set a dummy ID
        dto.setTitle("Test Title");

        // 2. Stub the repository to return empty for this specific ID
        // Using anyLong() is fine as long as the ID passed isn't null
        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

        // 3. Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> ticketService.fileTicket(dto));
    }

    // --- getTickets Coverage ---

    @Test
    void getTickets_ReturnsPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Ticket> page = new PageImpl<>(List.of(sampleTicket));

        when(ticketRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        Page<TicketResponseDTO> result = ticketService.getTickets(new TicketSearchDTO(), pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(TICKET_NO, result.getContent().get(0).getTicketNumber());
    }

    // --- assignTicket Coverage (Multiple Branches) ---

    @Test
    void assignTicket_Success() {
        Employee assignee = new Employee();
        assignee.setName(new FullName("Jane", null, "Smith"));

        when(ticketRepository.findById(TICKET_NO)).thenReturn(Optional.of(sampleTicket));
        when(employeeRepository.findById(2L)).thenReturn(Optional.of(assignee));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(sampleEmployee));
        when(ticketRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        TicketResponseDTO result = ticketService.assignTicket(TICKET_NO, 2L, 1L);

        assertEquals(TicketStatus.IN_PROGRESS, result.getStatus());
        assertEquals("Jane Smith", result.getAssigneeName());
        assertEquals("John Doe", result.getUpdatedBy());
    }

    @Test
    void assignTicket_TicketNotFound_ThrowsException() {
        lenient().when(ticketRepository.findById(anyString())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> ticketService.assignTicket("BAD", 1L, 1L));
    }

    // --- updateTicket Coverage (Null Branches) ---

    @Test
    void updateTicket_PartialUpdate_StatusOnly() {
        when(ticketRepository.findById(TICKET_NO)).thenReturn(Optional.of(sampleTicket));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(sampleEmployee));
        when(ticketRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        // remarks is null, testing "if(remarks != null)" branch
        TicketResponseDTO result = ticketService.updateTicket(TICKET_NO, TicketStatus.CLOSED, null, 1L);

        assertEquals(TicketStatus.CLOSED, result.getStatus());
        assertNull(result.getRemarks());
    }

    @Test
    void updateTicket_FullUpdate() {
        when(ticketRepository.findById(TICKET_NO)).thenReturn(Optional.of(sampleTicket));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(sampleEmployee));
        when(ticketRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        TicketResponseDTO result = ticketService.updateTicket(TICKET_NO, TicketStatus.DUPLICATE, "Fixed", 1L);

        assertEquals(TicketStatus.DUPLICATE, result.getStatus());
        assertEquals("Fixed", result.getRemarks());
    }

    // --- Mapping Logic Coverage ---

    @Test
    void mapToResponse_HandlesNullFields() {
        Ticket emptyTicket = new Ticket();
        emptyTicket.setTicketNumber("EMPTY");
        // No assignee, no creator, no name fields

        when(ticketRepository.findById("EMPTY")).thenReturn(Optional.of(emptyTicket));

        TicketResponseDTO result = ticketService.getTicketById("EMPTY");

        assertNull(result.getAssigneeName());
        assertNull(result.getCreatedByName());
    }
}