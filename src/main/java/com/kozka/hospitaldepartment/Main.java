package com.kozka.hospitaldepartment;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author Kozka Ivan
 */
public class Main {
    public static void main(String[] args) {
        PasswordEncoder e = new BCryptPasswordEncoder(8);
        System.out.println(e.encode("123"));
    }
}
