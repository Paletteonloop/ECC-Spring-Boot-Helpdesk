package com.carlolobitana.helpdesk.repository;

import com.carlolobitana.helpdesk.dto.TicketSearchDTO;
import com.carlolobitana.helpdesk.model.Ticket;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import java.util.ArrayList;
import java.util.List;

public class TicketSpecification {

    public static Specification<Ticket> buildFilter(TicketSearchDTO search) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (search.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), search.getStatus()));
            }

            if (search.getAssigneeId() != null) {
                predicates.add(cb.equal(root.get("assignee").get("id"), search.getAssigneeId()));
            }

            if (search.getCreatorId() != null) {
                predicates.add(cb.equal(root.get("createdBy").get("id"), search.getCreatorId()));
            }

            //LIKE '%keyword%'
            if (search.getTitleKeyword() != null && !search.getTitleKeyword().isEmpty()) {
                String pattern = "%" + search.getTitleKeyword().toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("title")), pattern));
            }

            //date range
            if (search.getStartDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdDate"), search.getStartDate().atStartOfDay()));
            }
            if (search.getEndDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdDate"), search.getEndDate().atTime(23, 59, 59)));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}