package lab2.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Football League API")
                        .version("1.0.0")
                        .description("""
                                RESTful API для управління футбольною лігою.
                                
                                ## Основні можливості:
                                - Управління командами (CRUD операції)
                                - Управління іграми та розкладом
                                - Управління результатами ігор
                                - Фільтрація та пагінація
                                - Часткові оновлення (JSON Patch RFC 6902, JSON Merge Patch RFC 7386)
                                
                                ## HTTP коди стану:
                                - **200** - Успішний запит
                                - **201** - Ресурс створено
                                - **204** - Успішно без вмісту (видалення)
                                - **400** - Некоректний запит
                                - **404** - Ресурс не знайдено
                                - **500** - Внутрішня помилка сервера
                                """)
                        .contact(new Contact()
                                .name("API Support")
                                .email("support@example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Development server")))
                .tags(List.of(
                        new Tag()
                                .name("Teams")
                                .description("Операції для управління командами"),
                        new Tag()
                                .name("Games")
                                .description("Операції для управління іграми"),
                        new Tag()
                                .name("Game Results")
                                .description("Операції для управління результатами ігор")));
    }
}
