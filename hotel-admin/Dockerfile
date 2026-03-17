FROM maven:3.9.4-eclipse-temurin-17 AS build

ARG DB_HOST=db
ARG DB_PORT=5432
ARG DB_NAME=hotelAdmin
ARG DB_USER=postgres
ARG DB_PASSWORD=ghghfrty

WORKDIR /build

COPY . .

# Подставляем параметры БД в config.properties (файл в модуле core, как в архиве)
RUN if [ -f core/src/main/resources/config.properties ]; then \
      sed -i "s|^db.url=.*|db.url=jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}|g" core/src/main/resources/config.properties && \
      sed -i "s|^db.user=.*|db.user=${DB_USER}|g" core/src/main/resources/config.properties && \
      sed -i "s|^db.password=.*|db.password=${DB_PASSWORD}|g" core/src/main/resources/config.properties ; \
    fi

# Собираем проект без тестов для ускорения
RUN mvn -T 1C -DskipTests clean package


FROM tomcat:9.0-jdk17

# Удаляем примеры и копируем собранный WAR как hotelAdmin.war
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /build/web/target/hotelAdmin.war /usr/local/tomcat/webapps/hotelAdmin.war

EXPOSE 8080

CMD ["catalina.sh", "run"]