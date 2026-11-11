package com.example.pf_back.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// @Configuration      -> o Spring lê esta classe na inicialização e registra os @Bean dela
// @EnableWebSecurity  -> liga o Spring Security (toda requisição passa pelos filtros de segurança)
// WebMvcConfigurer    -> permite customizar o Spring MVC (aqui usado só para o CORS)
@Configuration
@EnableWebSecurity
public class WebSecurityConfig implements WebMvcConfigurer {

    // CORS: libera o front (que roda em outro domínio, ex: Vercel ou localhost:5173)
    // a chamar esta API pelo navegador.
    // "/**"              -> vale para todas as rotas
    // allowedMethods("*") -> aceita qualquer método HTTP (GET, POST, PUT, DELETE, OPTIONS...)
    // Sem isso, o navegador bloqueia as requisições do front.
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedMethods("*");
    }

    // Define as regras de segurança: quem pode acessar cada rota e como o token é validado.
    // Toda requisição passa por esta cadeia de filtros antes de chegar no controller.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http    //permit all permite sem token
                //.authenticated() permite so com token

                // Regras de autorização por rota (avaliadas de cima para baixo; a primeira que bater vale):
                .authorizeHttpRequests(authorize -> authorize

                        // qualquer outra rota -> só precisa de um token válido (qualquer role)
                        .anyRequest().authenticated()
                )
                // CSRF desligado: a API é stateless (usa token JWT no header, não cookie de sessão),
                // então a proteção CSRF não é necessária e bloquearia os POSTs.
                .csrf(AbstractHttpConfigurer::disable)
                // Ativa o CORS no Spring Security usando a configuração do addCorsMappings acima.
                // Sem isso, o preflight (OPTIONS) do navegador seria barrado pela segurança.
                .cors(Customizer.withDefaults())
                // Resource Server: a API valida o token JWT emitido pelo Auth0.
                // O issuer-uri e o audience vêm do application.properties.
                // O jwtAuthenticationConverter diz de onde tirar as roles de dentro do token.
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                );

        return http.build();
    }

    // Converte o token JWT do Auth0 em um usuário autenticado com roles que o Spring entende.
    // O Auth0 coloca as roles num claim customizado; aqui dizemos qual é e como transformar.
    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authorities = new JwtGrantedAuthoritiesConverter();
        // Nome do claim do token onde estão as roles (configurado numa Action/Rule do Auth0).
        // Ex. no token: "https://social-insper.com/roles": ["ADMIN"]
        authorities.setAuthoritiesClaimName("https://social-insper.com/roles");
        // Prefixo "ROLE_": "ADMIN" vira "ROLE_ADMIN", que é o formato que hasRole("ADMIN") procura.
        authorities.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authorities);
        return converter;
    }
}
