package com.medicalrecords.service.user;

import com.medicalrecords.data.dto.RegistrationDTO;
import com.medicalrecords.data.entity.User;

public interface UserService {
    User createFromRegistration(RegistrationDTO dto, String keycloakUserId);
}
