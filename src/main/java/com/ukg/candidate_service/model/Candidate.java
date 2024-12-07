package com.ukg.candidate_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class Candidate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    @NotNull(message = "Job ID is required")
    private Long jobId;

    @NotBlank(message = "Status is required")
    private String status;

    @NotNull(message = "Applied at is required")
    private LocalDateTime appliedAt;
}