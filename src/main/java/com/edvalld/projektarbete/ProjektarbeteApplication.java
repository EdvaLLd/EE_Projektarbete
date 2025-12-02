package com.edvalld.projektarbete;

import com.edvalld.projektarbete.security.jwt.JwtUtils;
import com.edvalld.projektarbete.user.CustomUser;
import com.edvalld.projektarbete.user.CustomUserDetails;
import com.edvalld.projektarbete.user.authority.UserRole;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Set;


@SpringBootApplication
public class ProjektarbeteApplication {

	public static void main(String[] args) {

		SpringApplication.run(ProjektarbeteApplication.class, args);


		CustomUser benny = new CustomUser(
				"TESTUSER",
				"",
				true,
				true,
				true,
				true,
				Set.of(UserRole.USER, UserRole.ADMIN)
		);
		CustomUserDetails customUserDetails = new CustomUserDetails(benny);
		System.out.println("getAuthorities: " + customUserDetails.getAuthorities());
		JwtUtils jwtUtils = new JwtUtils();
		// Generate the token
		String token = jwtUtils.generateJwtToken(benny);
		System.out.println("Generated JWT:\n" + token);
		// Extract the roles
		Set<UserRole> extractedRoles = jwtUtils.getRolesFromJwtToken(token);
		System.out.println("Extracted roles: " + extractedRoles);
		System.out.println("Extracted username: " + jwtUtils.getUsernameFromJwtToken(token));

	}



}
