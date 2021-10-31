package com.kozka.hospitaldepartment;

import com.kozka.hospitaldepartment.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class HospitalDepartmentApplication {

    public static void main(String[] args) {
        SpringApplication.run(HospitalDepartmentApplication.class, args);
    }
}
