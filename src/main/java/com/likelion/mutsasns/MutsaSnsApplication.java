package com.likelion.mutsasns;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MutsaSnsApplication {

    public static void main(String[] args) {
        SpringApplication.run(MutsaSnsApplication.class, args);
    }

}
