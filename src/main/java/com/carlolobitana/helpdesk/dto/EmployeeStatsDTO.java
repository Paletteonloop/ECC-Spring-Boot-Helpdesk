package com.carlolobitana.helpdesk.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmployeeStatsDTO {
    private String employeeName;
    private long filedCount;
    private long inProgressCount;
    private long closedCount;
}