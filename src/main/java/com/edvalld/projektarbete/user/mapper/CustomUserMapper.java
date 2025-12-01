package com.edvalld.projektarbete.user.mapper;

import com.edvalld.projektarbete.user.CustomUser;
import com.edvalld.projektarbete.user.authority.UserRole;
import com.edvalld.projektarbete.user.dto.CustomUserCreationDTO;
import com.edvalld.projektarbete.user.dto.CustomUserResponseDTO;
import com.edvalld.projektarbete.user.dto.RegisterUserDTO;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class CustomUserMapper {

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

    public CustomUser toEntity(RegisterUserDTO registerUserDTO) {

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
