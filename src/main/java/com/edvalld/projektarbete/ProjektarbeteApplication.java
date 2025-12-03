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




	}



}
