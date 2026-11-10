
./mvnw clean package -DskipTests
//dependências para abaixar
Spring Web
Spring Data JPA
Validation
PostgreSQL Driver
Lombok
Testcontainers
Auth0

//entra na vm
ssh ubuntu@44.192.118.93 -i projsoft26b.pem

//criando a rede
docker network create -d bridge rede-pf

// cria a imagem
docker build -t guilhermetaba/pf-back .

//sobe a imagem
docker push guilhermetaba/pf-back


//reiniciar o banco do zero 
docker rm -f banco

docker run -d \
--name banco \
--network=rede-pf \
-e POSTGRES_DB=pfdb \
-e POSTGRES_USER=usuario \
-e POSTGRES_PASSWORD=senha \
postgres

docker restart pf-back

docker logs <container-do-app> --tail 50

docker network ls                       # lista as redes
docker network create rede-pf           # cria a rede

-p PORTA_DA_EC2:PORTA_DO_CONTAINER: a da esquerda é a porta da máquina, que o mundo acessa, e a da direita é a porta onde o Spring escuta dentro do container.



// ===== Action do Auth0 (Actions > Library > Create > Login flow) =====
// Quem nao tem role vira USER. O back aceita "roles" ou o claim com namespace.
exports.onExecutePostLogin = async (event, api) => {
  const roles = event.authorization?.roles?.length
    ? event.authorization.roles
    : ["USER"];

  api.accessToken.setCustomClaim("https://social-insper.com/roles", roles);
  api.idToken.setCustomClaim("https://social-insper.com/roles", roles);
};
// Depois de criar: Deploy + arrastar no Triggers > post-login.


# ================= CHECKLIST DA PROVA =================

## 1. Auth0
- [ ] Applications > APIs > Create API (Identifier = audience, ex: https://avaliacoes-api)
      -> usar esse valor em AUTH0_AUDIENCE (back) e VITE_AUTH0_AUDIENCE (front)
- [ ] Applications > Create Application > Single Page Application (pega o Client ID)
      Allowed Callback URLs / Logout URLs / Web Origins:
      http://localhost:5173, https://SEU-FRONT.vercel.app
- [ ] User Management > Roles: criar ADMIN e USER, atribuir ADMIN ao seu usuario
- [ ] Action acima criada, deployada e no trigger post-login

## 2. EC2 (uma vez so)
- [ ] ssh ubuntu@IP -i projsoft26b.pem
- [ ] docker network create -d bridge rede-pf
- [ ] subir o postgres "banco" (comando acima)
- [ ] Security Group: liberar porta 8080 (inbound)

## 3. GitHub Actions (repo do back > Settings > Secrets and variables > Actions)
- [ ] DOCKERHUB_TOKEN  (Docker Hub > Account settings > Personal access tokens)
- [ ] HOST_TEST        (IP publico da EC2)
- [ ] KEY_TEST         (conteudo inteiro do .pem)
- [ ] git push na master -> aba Actions verde -> docker ps na EC2
- [ ] Se trocou o issuer/audience: adicionar -e AUTH0_ISSUER_URI=... -e AUTH0_AUDIENCE=... no docker run do deploy.yml

## 4. Vercel (front)
- [ ] Import do repo pf-front, framework Vite
- [ ] Env vars: VITE_AUTH0_DOMAIN, VITE_AUTH0_CLIENT_ID, VITE_AUTH0_AUDIENCE, VITE_API_URL=/api
- [ ] vercel.json: trocar o IP da EC2 no rewrite de /api
- [ ] Redeploy depois de mudar env var (VITE_* entra no build)

## 5. Testar
- [ ] Login como ADMIN -> aparece formulario, cadastra, lista
- [ ] Login como usuario sem role -> so lista (POST da 403)
- [ ] Sem token -> 401
- [ ] Erro 401 com token? audience do front != audience do back
- [ ] Erro 403? roles nao estao no token (conferir em jwt.io)


# ================= VERSAO SEM ROLES =================
# Se a prova disser que NAO tem roles (qualquer usuario logado pode tudo):

## Back – WebSecurityConfig.java inteiro (sem o converter):

```java
package com.example.pf_back.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedMethods("*");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().authenticated()   // so precisa estar logado
                )
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        return http.build();
    }
}
```

## Misto (ex: uma rota publica, resto so logado):
    .requestMatchers(HttpMethod.GET, "/avaliacoes").permitAll()
    .anyRequest().authenticated()

## Front – App.jsx: mostrar o formulario pra todo mundo logado
    const isAdmin = true   // no lugar de: roles.includes('ADMIN')

## Auth0 – sem roles nao precisa da Action nem criar Roles.
## Continua precisando: API (audience) + SPA (client id) + callback URLs.
