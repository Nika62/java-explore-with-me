package ru.practicum.ewm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import ru.practicum.stats.client.HitClient;
import ru.practicum.stats.client.StatsClient;


@SpringBootApplication
@Import({HitClient.class, StatsClient.class})
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}