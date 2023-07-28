package ewm.server.config;

import ewm.client.StatsClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public StatsClient getStatsClient() {

        return new StatsClient();
    }
}