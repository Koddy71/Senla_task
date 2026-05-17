#Сборка
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

#Выполнение
FROM tomcat:10.1-jdk17
RUN rm -rf /usr/local/tomcat/webapps/* \ 
    && mkdir -p /usr/local/tomcat/logs
COPY --from=builder /app/target/user-service-*.war /usr/local/tomcat/webapps/ROOT.war
EXPOSE 8080
CMD ["catalina.sh", "run"]