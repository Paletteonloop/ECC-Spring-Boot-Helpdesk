package com.carlolobitana.helpdesk.repository;

import com.carlolobitana.helpdesk.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, String>, JpaSpecificationExecutor<Ticket> {

    @Query(value = "SELECT COUNT(*) FROM ticket t WHERE t.assignee_id = :empId AND t.status = :status",
            nativeQuery = true)
    long countByAssigneeAndStatus(@Param("empId") Long empId, @Param("status") String status);
}