package in.gov.abdm.fhir;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the ABDM FHIR Interoperability Service.
 *
 * <p>This service converts hospital data into HL7 FHIR R4 resources
 * and is designed to be the FHIR layer for ABDM M2/M3 HIP/HIU implementations.</p>
 */
@SpringBootApplication
public class AbdmFhirServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AbdmFhirServiceApplication.class, args);
    }
}
