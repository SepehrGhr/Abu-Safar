FROM maven:3.9.6-eclipse-temurin-23-jammy AS build

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline

COPY src ./src

RUN mvn package -DskipTests

FROM eclipse-temurin:23-jre-jammy

WORKDIR /app

RUN addgroup --system abusafar && adduser --system --ingroup abusafar abusafar
USER abusafar

COPY --from=build /app/target/AbuSafar-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8888

ENTRYPOINT ["java", "-jar", "app.jar"]