package com.zkAttendance.demo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class BiometricFileImportService {

    private final ZohoAttendanceService attendanceService;

    private static final Pattern ATTENDANCE_PATTERN = Pattern.compile(
            "^(\\d+)\\s+(\\d{4}-\\d{2}-\\d{2}\\s+\\d{2}:\\d{2}:\\d{2}).*\\s([IO])\\s+\\d+\\s+\\d+$"
    );

    public void importFile(Path filePath) throws IOException {

        List<String> lines = Files.readAllLines(filePath);

        for (int i = 1; i < lines.size(); i++) {

            String line = lines.get(i).trim();
            if (line.isEmpty()) continue;

            Matcher matcher = ATTENDANCE_PATTERN.matcher(line);

            if (!matcher.matches()) {
                log.warn("Invalid format line skipped: {}", line);
                continue;
            }

            String empCode = matcher.group(1);
            String dateTime = matcher.group(2);
            String ioType = matcher.group(3);

            try {
                attendanceService.processAttendanceFromBiometric(empCode, dateTime, ioType);
            } catch (Exception e) {
                log.error("Error processing line: {}", line, e);
            }
        }

        log.info("Attendance file processed successfully.");
    }
}