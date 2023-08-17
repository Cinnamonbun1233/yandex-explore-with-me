# Yandex-Explore-With-Me

## Стек

Java Core, Spring Boot, Spring Framework, Git, Maven, PostgreSQL, Hibernate, Docker, Swagger.

## Для чего предназначен данный проект
Yandex-Explore-With-Me - сервис-афиша, где можно предложить какое-либо событие от выставки до похода в кино и набрать компанию для участия в нём

## _Swagger API Specification_
- Main service - https://raw.githubusercontent.com/cinnamonbun1233/java-explore-with-me/main/ewm-main-service-spec.json
- Statistic service - https://raw.githubusercontent.com/cinnamonbun1233/java-explore-with-me/main/ewm-stats-service-spec.json

## _Docker start-up guide_
1. mvn clean package
2. mvn install
3. docker-compose build
4. docker-compose up -d
5. Main service: http://localhost:8080
6. Statistic service: http://localhost:9090
