import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@ComponentScan("ewm/server")
@EnableJpaRepositories
@EnableTransactionManagement
@EntityScan("ewm/server")
public class MainServiceServerApp {
    public static void main(String[] args) {
        SpringApplication.run(MainServiceServerApp.class, args);
    }
}