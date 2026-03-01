# ZKAttendance to Zoho People Integration

This Spring Boot application serves as a middleware to synchronize attendance data between a ZK/Biometric attendance system and **Zoho People**. It handles OAuth 2.0 authentication with Zoho automatically and exposes REST endpoints to push attendance entries (Check-in/Check-out).

## Features

- **OAuth 2.0 Management**: Automatically handles Access Token generation and refreshing using a Zoho Refresh Token.
- **Manual Toggle**: Simple endpoint to toggle Check-in/Check-out status for an employee ID.
- **Time Formatting**: Automatically converts biometric timestamp formats to Zoho's required format.

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- A valid Zoho People account
- A Zoho API Application (Client ID, Client Secret, and Refresh Token)

## Configuration

The application uses `application.properties` to configure Zoho credentials. For security, it is recommended to use Environment Variables rather than hardcoding credentials.

### Environment Variables
Set the following environment variables on your server or IDE:

| Variable | Description |
| :--- | :--- |
| `ZOHO_CLIENT_ID` | Your Zoho API Client ID |
| `ZOHO_CLIENT_SECRET` | Your Zoho API Client Secret |
| `ZOHO_REFRESH_TOKEN` | Your generated Zoho Refresh Token |

### Application Properties
Located in `src/main/resources/application.properties`:

```properties
spring.application.name=zkAttendance

# Credentials mapped to Environment Variables
zoho.client.id=${ZOHO_CLIENT_ID}
zoho.client.secret=${ZOHO_CLIENT_SECRET}
zoho.refresh.token=${ZOHO_REFRESH_TOKEN}

# Zoho Endpoints
zoho.token.url=https://accounts.zoho.com/oauth/v2/token
zoho.api.url=https://people.zoho.com/people/api/attendance
```

### Installation & Running

  Clone the repository:

    bash

    git clone https://github.com/your-username/zk-attendance-demo.git
    cd zk-attendance-demo

  Build the project:

    bash

    mvn clean install

  Run the application:

    bash

    mvn spring-boot:run

  The application will start on port 8080 (default).

### API Documentation
1. Manual Attendance Toggle

Toggles the attendance state for an employee. If they are not in the local cache, it checks them in. If they are checked in, it checks them out.

    Endpoint: POST /api/attendance/attend
    Query Parameter: empId (String)

Example Request:

bash

curl -X POST "http://localhost:8080/api/attendance/attend?empId=EMP1001"

Response:
```
  json
  
  {
      "success": true,
      "employeeId": "EMP1001",
      "timestamp": "2023-10-27T09:00:00",
      "zohoResponse": "{...Zoho API Response...}"
  }
```
2. Biometric Data Sync
```
    Endpoint: POST /api/attendance/attend/biometric
    Content-Type: application/json
```
example request:
The firstInTime and lastOutTime must be in the format yyyy-MM-dd HH:mm:ss.
```
json
{
    "personnelId": "1",
    "firstName": "user1",
    "lastName": null,
    "firstInReaderName": "192.168.101.158-1-In",
    "firstInTime": "2026-02-10 09:15:55",
    "lastOutReaderName": "192.168.101.158-1-In",
    "lastOutTime": "2026-02-10 15:15:55",
    "departmentName": "IT Department"
}
```


Response:
```
json

{
    "timestamp": "10-02-2026 14:06:54",
    "responses": [
        "Check-in processed: [{\"inputType_in\":3,\"dateFormat\":\"dd-MM-yyyy%20HH:mm:ss\",\"tempTsecs\":0,\"response\":\"success\",\"fdate\":\"11-02-2026%2009:15:55\",\"additional_status\":\"In\",\"tsecs\":\"0\",\"punchIn\":\"11-02-2026%2009:15:55\"}]",
        "Check-out processed: [{\"inputType_out\":3,\"tdate\":\"11-02-2026%2015:15:55\",\"dateFormat\":\"dd-MM-yyyy%20HH:mm:ss\",\"response\":\"success\",\"fdate\":\"11-02-2026%2009:15:00\",\"tsecs\":21600}]"
    ],
    "employeeId": "1",
    "employeeName": "user1 null",
    "department": "IT Department",
    "success": true
}
```
Project Structure
```
text

src/main/java/com/zkAttendance/demo
├── controller
│   └── AttendanceController.java  # REST Endpoints
├── dto
│   |── BiometricRequest.java      # JSON Data Transfer Object
|   └── BiometricFileImportService # For Importing txt file
├── service
│   ├── ZohoAttendanceService.java # Business Logic & API Calls
│   └── ZohoTokenService.java      # OAuth Token Management
└── util
    └── TimeUtil.java              # Helper for timestamps
```
### References

  - [zoho peope attendance entries](https://www.zoho.com/people/api/attendance-entries.html)
  - [zoho api console](https://api-console.zoho.com/)
    
