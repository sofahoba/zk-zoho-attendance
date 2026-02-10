package com.zkAttendance.demo.controller;

import com.zkAttendance.demo.dto.BiometricRequest;
import com.zkAttendance.demo.service.ZohoAttendanceService;
import com.zkAttendance.demo.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor

public class AttendanceController {

    private final ZohoAttendanceService attendanceService;

    @PostMapping("/attend")
    public ResponseEntity<?> attend(@RequestParam String empId) {

        String zohoResponse = attendanceService.attend(empId);

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "employeeId", empId,
                        "timestamp", TimeUtil.now(),
                        "zohoResponse", zohoResponse
                )
        );
    }

    @PostMapping("/attend/biometric")
    public ResponseEntity<?> attendWithBiometric(@RequestBody BiometricRequest biometricRequest) {

        List<String> responses = attendanceService.attendWithBiometricData(biometricRequest);

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "employeeId", biometricRequest.getPersonnelId(),
                        "employeeName", biometricRequest.getFirstName() + " " + biometricRequest.getLastName(),
                        "department", biometricRequest.getDepartmentName(),
                        "timestamp", TimeUtil.now(),
                        "responses", responses
                )
        );
    }
}