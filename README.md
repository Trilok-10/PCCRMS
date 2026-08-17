(PCCRMS) Patient Care Clinical Record and Management Systems  - 
 a modular Spring Boot microservices Patient Care & Clinical Records Management System using Eureka service discovery, Spring Cloud Gateway with JWT-based auth, OpenFeign inter-service communication, and multi-schema MySQL   persistence. Built service-specific bounded contexts (Auth, Patients, Appointments, EHR, Pharmacy, Billing), implemented API gateway authentication + header propagation, cascade deletion flows, and a responsive registration frontend.

 
 Overall architecture & tech stack
 
• Multi-module Maven Spring Boot microservices (root pom lists modules).

• Modules: eureka-server (service discovery), api-gateway (Spring Cloud Gateway + auth filter), auth-service (user management + JWT), patient-service, appointment-service, ehr-service, pharmacy-service, billing-service, plus a small static frontend.

• Common tech: Java 17, Spring Boot, Spring Cloud (Eureka, Gateway), Spring Cloud OpenFeign, Spring Data JPA, MySQL (each service configured with its own DB), jjwt for JWT, Lombok, Maven.

• Communication: Services discover via Eureka; API Gateway performs routing + JWT validation and injects user info headers; services use Feign clients for direct inter-service calls (cascade deletes, cross-service flows).

• Persistence: Each bounded service uses JPA entities + JpaRepository; application.properties show MySQL URLs per service (e.g., patient_db, appointment_db, ehr_db, pharmacy_db, billing_db, auth_db).

• Frontend: simple static registration page (frontend/register.html) calling auth endpoints.
