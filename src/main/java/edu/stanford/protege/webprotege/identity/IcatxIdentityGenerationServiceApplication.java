package edu.stanford.protege.webprotege.identity;

import edu.stanford.protege.webprotege.ipc.WebProtegeIpcApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableConfigurationProperties
@ConfigurationPropertiesScan
@Import(WebProtegeIpcApplication.class)
@EnableRetry
public class IcatxIdentityGenerationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(IcatxIdentityGenerationServiceApplication.class, args);
	}

}
