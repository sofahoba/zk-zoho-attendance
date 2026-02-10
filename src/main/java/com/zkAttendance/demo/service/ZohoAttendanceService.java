package com.zkAttendance.demo.service;

import com.zkAttendance.demo.dto.BiometricRequest;
import com.zkAttendance.demo.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class ZohoAttendanceService {

    @Value("${zoho.api.url}")
    private String attendanceApiUrl;

    private final ZohoTokenService tokenService;
    private final RestTemplate restTemplate = new RestTemplate();
    private final Map<String, Boolean> attendanceState = new ConcurrentHashMap<>();

    private static final DateTimeFormatter BIOMETRIC_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter ZOHO_FORMAT =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public String attend(String empId) {
        boolean isCheckedIn = attendanceState.getOrDefault(empId, false);

        if (!isCheckedIn) {
            String response = callAttendanceApi(empId, TimeUtil.now(), null);
            attendanceState.put(empId, true);
            return response;
        } else {
            String response = callAttendanceApi(empId, null, TimeUtil.now());
            attendanceState.put(empId, false);
            return response;
        }
    }

    public List<String> attendWithBiometricData(BiometricRequest biometricRequest) {
        List<String> responses = new ArrayList<>();
        String empId = biometricRequest.getPersonnelId();

        try {
            if (biometricRequest.getFirstInTime() != null && !biometricRequest.getFirstInTime().isEmpty()) {
                String checkInTime = convertTimeFormat(biometricRequest.getFirstInTime());
                String checkInResponse = callAttendanceApi(empId, checkInTime, null);
                responses.add("Check-in processed: " + checkInResponse);
                attendanceState.put(empId, true);
            }

            if (biometricRequest.getLastOutTime() != null && !biometricRequest.getLastOutTime().isEmpty()) {
                String checkOutTime = convertTimeFormat(biometricRequest.getLastOutTime());
                String checkOutResponse = callAttendanceApi(empId, null, checkOutTime);
                responses.add("Check-out processed: " + checkOutResponse);
                attendanceState.put(empId, false);
            }

        } catch (Exception e) {
            log.error("Error processing biometric attendance for employee: {}", empId, e);
            responses.add("Error: " + e.getMessage());
        }

        return responses;
    }

    private String convertTimeFormat(String biometricTime) {
        LocalDateTime dateTime = LocalDateTime.parse(biometricTime, BIOMETRIC_FORMAT);
        return dateTime.format(ZOHO_FORMAT);
    }

    private String callAttendanceApi(String empId, String checkIn, String checkOut) {
        String dateFormat = "dd-MM-yyyy HH:mm:ss";

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(attendanceApiUrl)
                .queryParam("dateFormat", dateFormat)
                .queryParam("empId", empId);

        if (checkIn != null) {
            builder.queryParam("checkIn", checkIn);
        }
        if (checkOut != null) {
            builder.queryParam("checkOut", checkOut);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Zoho-oauthtoken " + tokenService.getAccessToken());

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.POST,
                entity,
                String.class
        );

        return response.getBody();
    }
}