package com.medicalrecords.service.user;


import com.medicalrecords.data.dto.RegistrationDTO;
import com.medicalrecords.data.entity.User;
import com.medicalrecords.data.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User createFromRegistration(RegistrationDTO dto, String keycloakUserId) {
        User user = new User();
        user.setKeycloakId(keycloakUserId);
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setRole(dto.getRole());
        return userRepository.save(user);
    }
}