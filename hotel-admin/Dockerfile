FROM maven:3.9.4-eclipse-temurin-17 AS build

WORKDIR /build
COPY . .

# Собираем проект без тестов для ускорения
RUN mvn -T 1C -DskipTests clean package


FROM tomcat:9.0-jdk17
# Удаляем примеры и копируем собранный WAR как hotelAdmin.war
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /build/web/target/hotelAdmin.war /usr/local/tomcat/webapps/hotelAdmin.war

EXPOSE 8080
CMD ["catalina.sh", "run"]