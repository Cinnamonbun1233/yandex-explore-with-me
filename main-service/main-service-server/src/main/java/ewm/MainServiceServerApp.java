package ewm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableJpaRepositories
@EnableTransactionManagement
//@ComponentScan("ewm/server")
@EntityScan("ewm/server")
public class MainServiceServerApp {
    public static void main(String[] args) {
        SpringApplication.run(MainServiceServerApp.class, args);
    }
}