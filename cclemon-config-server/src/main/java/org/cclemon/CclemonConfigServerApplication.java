package org.cclemon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class CclemonConfigServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CclemonConfigServerApplication.class, args);
	}

}
