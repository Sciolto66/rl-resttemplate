package nl.rowendu.rlresttemplate.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.client.RestTemplateBuilderConfigurer;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class RestTemplateBuilderConfig {

  @Value("${rest.template.rootUrl}")
  String rootUrl;

  @Value("${spring.security.user.name}")
  String username;

  @Value("${spring.security.user.password}")
  String password;

  @Bean
  RestTemplateBuilder restTemplateBuilder(RestTemplateBuilderConfigurer configurer) {

    assert rootUrl != null;

    return configurer
        .configure(new RestTemplateBuilder())
        .basicAuthentication(username, password)
        .uriTemplateHandler(new DefaultUriBuilderFactory(rootUrl));
  }
}
