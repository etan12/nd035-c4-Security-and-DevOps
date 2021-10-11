package com.example.demo;

import com.splunk.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EnableJpaRepositories("com.example.demo.model.persistence.repositories")
@EntityScan("com.example.demo.model.persistence")
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class })
public class SareetaApplication {

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public Receiver splunkServiceReceiver() {
		HttpService.setSslSecurityProtocol(SSLSecurityProtocol.TLSv1_2);

		ServiceArgs loginArgs = new ServiceArgs();
		loginArgs.setUsername("etan432");
		loginArgs.setPassword("Udemy2021!");
		loginArgs.setHost("127.0.0.1");
		loginArgs.setPort(8089);
		loginArgs.setScheme("https");

		Service service = Service.connect(loginArgs);
		Receiver receiver = service.getReceiver();

		return receiver;
	}

	public static void main(String[] args) {
		SpringApplication.run(SareetaApplication.class, args);
	}

}
