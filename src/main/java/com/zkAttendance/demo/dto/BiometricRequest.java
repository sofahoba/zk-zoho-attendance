package com.zkAttendance.demo.dto;

import lombok.Data;

@Data
public class BiometricRequest {
    private String personnelId;
    private String firstName;
    private String lastName;
    private String firstInReaderName;
    private String firstInTime;
    private String lastOutReaderName;
    private String lastOutTime;
    private String departmentName;
}