package com.edvalld.projektarbete.user.mapper;

import com.edvalld.projektarbete.user.CustomUser;
import com.edvalld.projektarbete.user.authority.UserRole;
import com.edvalld.projektarbete.user.dto.CustomUserCreationDTO;
import com.edvalld.projektarbete.user.dto.CustomUserResponseDTO;
import com.edvalld.projektarbete.user.dto.RegisterUserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class CustomUserMapper {

    private static final Logger log = LoggerFactory.getLogger(CustomUserMapper.class);

    public CustomUser toEntity(CustomUserCreationDTO customUserCreationDTO) {

        return new CustomUser(
                customUserCreationDTO.username(),
                customUserCreationDTO.password(),
                customUserCreationDTO.isAccountNonExpired(),
                customUserCreationDTO.isAccountNonLocked(),
                customUserCreationDTO.isCredentialsNonExpired(),
                customUserCreationDTO.isEnabled(),
                customUserCreationDTO.roles()
        );
    }

    public CustomUserResponseDTO toUsernameDTO(CustomUser customUser) {

        return new CustomUserResponseDTO(customUser.getUsername());
    }

    @Value("${admin.pass}")
    private String adminPass;

    public CustomUser toEntity(RegisterUserDTO registerUserDTO) {



        if(registerUserDTO.adminPass().equals(adminPass))
        {
            log.info("Created user '{}' as an admin", registerUserDTO.username());
            return new CustomUser(
                    registerUserDTO.username(),
                    registerUserDTO.password(),
                    true,
                    true,
                    true,
                    true,
                    Set.of(UserRole.USER, UserRole.ADMIN)
            );
        }
        log.info("Created user '{}' as a user", registerUserDTO.username());
        return new CustomUser(
                registerUserDTO.username(),
                registerUserDTO.password(),
                true,
                true,
                true,
                true,
                Set.of(UserRole.USER)
        );
    }

}
