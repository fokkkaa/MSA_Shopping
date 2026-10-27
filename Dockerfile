FROM eclipse-temurin:21-jre-jammy
WORKDIR /opt/shopping
RUN groupadd --system shopping && useradd --system --gid shopping shopping
ARG JAR_FILE=target/MSA_shopping-0.0.1-SNAPSHOT.jar
COPY --chown=shopping:shopping ${JAR_FILE} app.jar
USER shopping
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
