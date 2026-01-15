FROM maven:3.9.8-eclipse-temurin-21 AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests



FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
COPY --from=build /app/target/classes/ssl/E7_ISRG_Root_X1.cer E7_ISRG_Root_X1.cer

RUN keytool -importcert -noprompt  \
    -alias E7_ISRG_Root_X1-ca \
    -file /app/E7_ISRG_Root_X1.cer \
    -keystore $JAVA_HOME/lib/security/cacerts \
    -storepass changeit

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]