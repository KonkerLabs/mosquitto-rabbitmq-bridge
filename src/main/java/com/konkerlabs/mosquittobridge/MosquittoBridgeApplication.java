package com.konkerlabs.mosquittobridge;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MosquittoBridgeApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(MosquittoBridgeApplication.class, args);
    }

    public void run(String... args) throws Exception {
    }

}