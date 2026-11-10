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