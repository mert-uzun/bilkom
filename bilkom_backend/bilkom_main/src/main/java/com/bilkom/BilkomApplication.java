package tr.edu.bilkent.bilkom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BilkomApplication {
    public static void main(String[] args) {
        SpringApplication.run(BilkomApplication.class, args);
    }
} 