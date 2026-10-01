FROM eclipse-temurin:25-jdk AS build

WORKDIR /app

COPY pom.xml aot-jar.properties openapi.properties ./
COPY docs/ docs/
COPY src/ src/

RUN apt-get update && apt-get install -y maven && \
    mvn package -DskipTests -B --no-transfer-progress -T 0.5C

FROM eclipse-temurin:25-jre

WORKDIR /app

COPY --from=build /app/target/portfolioai-0.1.jar /app/portfolioai.jar

ENV JAVA_OPTS="-Xmx220m -XX:+UseSerialGC -Xss512k"

EXPOSE 10000

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/portfolioai.jar"]