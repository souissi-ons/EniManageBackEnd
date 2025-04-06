package tn.enicarthage.enimanage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "tn.enicarthage.enimanage.repository")
public class EniManageApplication {
	public static void main(String[] args) {
		SpringApplication.run(EniManageApplication.class, args);
	}
}