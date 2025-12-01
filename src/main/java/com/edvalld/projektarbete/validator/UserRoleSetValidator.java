package com.edvalld.projektarbete.validator;


import com.edvalld.projektarbete.user.authority.UserRole;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Set;



public class UserRoleSetValidator implements ConstraintValidator<ValidRoles, Set<UserRole>> {

    private static final Set<UserRole> ALLOWED = Set.of(UserRole.GUEST, UserRole.USER, UserRole.ADMIN);

    @Override
    public boolean isValid(Set<UserRole> roles, ConstraintValidatorContext context) {
        return roles != null && ALLOWED.containsAll(roles);
    }
}
