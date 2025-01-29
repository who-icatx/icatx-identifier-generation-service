package edu.stanford.protege.webprotege.identity;

import edu.stanford.protege.webprotege.ipc.WebProtegeIpcApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(WebProtegeIpcApplication.class)
public class IcatxIdentityGenerationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(IcatxIdentityGenerationServiceApplication.class, args);
	}

}
