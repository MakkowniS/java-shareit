package ru.practicum.shareit;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class ShareItGateway {
	public static void main(String[] args) {
		SpringApplication.run(ShareItGateway.class, args);
	}

	@PostConstruct
	public void init() {
		// Принудительно устанавливаем временную зону для всего приложения
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

}
