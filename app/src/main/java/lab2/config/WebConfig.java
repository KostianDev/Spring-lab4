package lab2.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    public static final String APPLICATION_JSON_PATCH = "application/json-patch+json";
    public static final String APPLICATION_MERGE_PATCH = "application/merge-patch+json";

    public static final MediaType APPLICATION_JSON_PATCH_TYPE = MediaType.parseMediaType(APPLICATION_JSON_PATCH);
    public static final MediaType APPLICATION_MERGE_PATCH_TYPE = MediaType.parseMediaType(APPLICATION_MERGE_PATCH);

    @Bean
    @Primary
    ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer
                .mediaType("json-patch+json", APPLICATION_JSON_PATCH_TYPE)
                .mediaType("merge-patch+json", APPLICATION_MERGE_PATCH_TYPE);
    }
}
