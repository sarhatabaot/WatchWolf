# syntax=docker/dockerfile:1.7

FROM eclipse-temurin:17-jdk AS plugin_build
COPY --from=server_source / /src/server
WORKDIR /src/server
RUN sed -i 's/\r$//' gradlew && bash ./gradlew --no-daemon assemble

FROM maven:3.9-eclipse-temurin-17 AS manager_build
COPY --from=manager_source / /src/manager
WORKDIR /src/manager
RUN mkdir -p lib \
    && curl -fL https://github.com/watch-wolf/WatchWolf-Core/releases/download/v0.3.1/watchwolf-core-0.3.1.jar \
        -o lib/watchwolf-core-0.3.1.jar \
    && mvn -B org.apache.maven.plugins:maven-install-plugin:3.1.4:install-file \
        -Dfile=lib/watchwolf-core-0.3.1.jar -DgroupId=dev.watchwolf \
        -DartifactId=watchwolf-core -Dversion=0.3.1 -Dpackaging=jar \
    && mvn -B -DskipTests package

FROM eclipse-temurin:17-jdk
WORKDIR /servers
COPY --from=manager_build /src/manager/target/watchwolf-servers-manager-0.3.3.jar ServersManager.jar
COPY --from=plugin_build /src/server/build/libs/WatchWolf-0.3.1-1.8-LATEST.jar usual-plugins/
RUN mkdir -p server-types tmp logs
CMD ["java", "-cp", "ServersManager.jar", "dev.watchwolf.serversmanager.ServersManager"]
