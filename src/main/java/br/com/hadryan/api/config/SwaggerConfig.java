package br.com.hadryan.api.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "YouTube Downloader API",
                version = "v1",
                description = "API para download de vídeos do YouTube"
        )
)
public class SwaggerConfig {

}
