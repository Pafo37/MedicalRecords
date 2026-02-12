package com.medicalrecords.data.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "app_user")
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity{

    @NotNull(message = "Keycloak id must not be null")
    @Column(nullable = false, unique = true)
    private String keycloakId;

    @NotNull(message = "Username must not be null")
    @Column(nullable = false, unique = true)
    private String username;

    @NotNull(message = "Email must not be null")
    @Email(message = "Please provide a valid email address")
    @Column(nullable = false, unique = true)
    private String email;

    @Size(min = 3, message = "Minimum characters must be 3")
    @NotNull(message = "First name must not be null")
    @Column(nullable = false)
    private String firstName;


    @Size(min = 3, message = "Minimum characters must be 3")
    @NotNull(message = "Last name must not be null")
    @Column(nullable = false)
    private String lastName;

    @NotNull(message = "Role must not be null")
    @Column(nullable = false)
    private String role;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Patient patient;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Doctor doctor;
}