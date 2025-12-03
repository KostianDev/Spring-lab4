package lab2.config;

import lab2.service.NotificationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class AppConfig {

    @Bean
    public NotificationService emailNotificationService() {
        return new NotificationService("EMAIL", "smtp.example.com");
    }

    @Bean
    @Scope("prototype")
    public NotificationService smsNotificationService() {
        return new NotificationService("SMS", "sms-gateway.example.com");
    }
}
