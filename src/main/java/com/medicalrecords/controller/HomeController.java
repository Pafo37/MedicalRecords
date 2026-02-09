package com.medicalrecords.controller;

import com.medicalrecords.data.entity.Patient;
import com.medicalrecords.data.entity.User;
import com.medicalrecords.data.repository.PatientRepository;
import com.medicalrecords.data.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;

    @GetMapping("/")
    public String home(@AuthenticationPrincipal OidcUser oidcUser, Model model) {

        if (oidcUser == null) {
            return "home";
        }

        String keycloakId = oidcUser.getSubject();

        Optional<User> localUser = userRepository.findByKeycloakId(keycloakId);

        if (localUser.isPresent() && "ROLE_PATIENT".equals(localUser.get().getRole())) {

            Optional<Patient> patientRecord =
                    patientRepository.findByUser(localUser.get());

            boolean insurancePaidLast6Months =
                    patientRecord.map(Patient::isInsurancePaidLast6Months)
                            .orElse(false);

            model.addAttribute("insurancePaidLast6Months",
                    insurancePaidLast6Months);
        }

        return "home";
    }
}