# ABDM FHIR Service

A standalone HL7 FHIR R4 interoperability service built with Spring Boot 3 and HAPI FHIR.

This service converts hospital data into standards-compliant FHIR R4 resources and is designed
to serve as the FHIR layer for ABDM M2/M3 HIP/HIU implementations.

---

## Tech Stack

| Layer        | Technology                    |
|--------------|-------------------------------|
| Language     | Java 21                       |
| Framework    | Spring Boot 3.3.4             |
| FHIR Library | HAPI FHIR 7.4.0 (R4)         |
| Build        | Maven                         |
| Utilities    | Lombok, Jakarta Validation    |

---

## Project Structure

```
abdm-fhir-service/
└── src/main/java/in/gov/abdm/fhir/
    ├── AbdmFhirServiceApplication.java   # Entry point
    ├── config/
    │   └── FhirConfig.java               # FhirContext + IParser beans
    ├── controller/
    │   └── PatientFhirController.java    # POST /fhir/patient
    ├── dto/
    │   └── PatientDTO.java               # Input contract (validated)
    ├── exception/
    │   ├── ErrorResponse.java            # Uniform error payload
    │   ├── GlobalExceptionHandler.java   # @RestControllerAdvice
    │   └── InvalidDateException.java     # Custom exception
    ├── mapper/
    │   └── PatientFhirMapper.java        # DTO → FHIR Patient resource
    └── service/
        └── PatientFhirService.java       # Orchestration + serialisation
```

---

## Running the Service

```bash
cd abdm-fhir-service
mvn spring-boot:run
```

The service starts on **http://localhost:8080**.

---

## Running Tests

```bash
mvn test
```

---

## API Reference

### POST /fhir/patient

Converts hospital patient data to an HL7 FHIR R4 Patient resource.

| Property    | Value                      |
|-------------|----------------------------|
| Method      | `POST`                     |
| URL         | `/fhir/patient`            |
| Consumes    | `application/json`         |
| Produces    | `application/fhir+json`    |

#### Request Body (PatientDTO)

```json
{
  "patientId": "PAT-001",
  "fullName": "Ravi Kumar",
  "gender": "male",
  "dateOfBirth": "1990-05-15",
  "phoneNumber": "9876543210",
  "address": "12, MG Road, Bengaluru, Karnataka 560001",
  "abhaNumber": "91-1234-5678-9012"
}
```

| Field         | Required | Validation                                      |
|---------------|----------|-------------------------------------------------|
| `patientId`   | ✅ Yes   | Not blank                                       |
| `fullName`    | ✅ Yes   | Not blank                                       |
| `gender`      | ✅ Yes   | One of: `male`, `female`, `other`, `unknown`    |
| `dateOfBirth` | ✅ Yes   | Not blank, format `yyyy-MM-dd`                  |
| `phoneNumber` | ❌ No    | —                                               |
| `address`     | ❌ No    | —                                               |
| `abhaNumber`  | ❌ No    | —                                               |

#### Response (200 OK)

```json
{
  "resourceType": "Patient",
  "id": "PAT-001",
  "identifier": [
    {
      "use": "usual",
      "system": "https://hospital.example.org/patients",
      "value": "PAT-001"
    },
    {
      "use": "official",
      "system": "https://healthid.ndhm.gov.in",
      "value": "91-1234-5678-9012"
    }
  ],
  "name": [
    {
      "use": "official",
      "text": "Ravi Kumar"
    }
  ],
  "telecom": [
    {
      "system": "phone",
      "value": "9876543210",
      "use": "home"
    }
  ],
  "gender": "male",
  "birthDate": "1990-05-15",
  "address": [
    {
      "use": "home",
      "text": "12, MG Road, Bengaluru, Karnataka 560001"
    }
  ]
}
```

#### Error Responses

**400 Bad Request — Validation failure**
```json
{
  "status": 400,
  "error": "Validation Failed",
  "message": "One or more fields failed validation. See fieldErrors for details.",
  "timestamp": "2024-11-01T10:30:00Z",
  "fieldErrors": [
    {
      "field": "fullName",
      "rejectedValue": null,
      "message": "Full name must not be blank"
    }
  ]
}
```

**400 Bad Request — Invalid date**
```json
{
  "status": 400,
  "error": "Invalid Date",
  "message": "Invalid dateOfBirth value: '2024-02-30'. Expected yyyy-MM-dd.",
  "timestamp": "2024-11-01T10:30:00Z"
}
```

---

## Postman Quick Start

1. Open Postman.
2. Create a new **POST** request to `http://localhost:8080/fhir/patient`.
3. Set **Headers**: `Content-Type: application/json`.
4. Paste the example request body above into the **Body → raw** tab.
5. Send — you should receive a `200 OK` with a valid FHIR Patient JSON.

---

## Future Phases

The architecture is designed for incremental extension. Adding a new resource requires only:

1. A new `XxxDTO` in `dto/`
2. A new `XxxFhirMapper` in `mapper/`
3. A new `XxxFhirService` in `service/`
4. A new `XxxFhirController` in `controller/`

No changes are needed to the Patient implementation.

Planned resources:
- `Practitioner`
- `Observation`
- `MedicationRequest`
- `DiagnosticReport`
- `DocumentReference`
- `Encounter`
- `Bundle`
