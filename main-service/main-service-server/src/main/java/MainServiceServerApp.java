import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("ewm_client")
public class MainServiceServerApp {
    public static void main(String[] args) {
        SpringApplication.run(MainServiceServerApp.class, args);
    }
}