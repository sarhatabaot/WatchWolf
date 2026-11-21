# The build context is the WatchWolfX monorepo root.

# Stage 1: Build the local WatchWolfX Core artifact used by both Java services.
FROM maven:3.9-eclipse-temurin-8 AS core_build
WORKDIR /src/core
COPY watchwolfx-core/ .
RUN mvn --batch-mode --no-transfer-progress -DskipTests clean package

# Stage 2: Build ServersManager jar.
FROM gradle:9.7.1-jdk17 AS manager_build
USER root
WORKDIR /src/manager
COPY watchwolfx-servermanager/ .
COPY --from=core_build /src/core/target/watchwolfx-core-0.5.0.jar lib/watchwolfx-core-0.5.0.jar
RUN gradle --no-daemon shadowJar -x test

# Create the final required directory structure in this stage.
RUN mkdir -p /out/server-types /out/tmp /out/logs /out/usual-plugins

# Stage 3: Build the matching local WatchWolfX Server plugin.
FROM gradle:9.7.1-jdk17 AS plugin_build
USER root
WORKDIR /src/server
COPY watchwolfx-server/ .
COPY --from=core_build /src/core/target/watchwolfx-core-0.5.0.jar lib/watchwolfx-core-0.5.0.jar
RUN gradle --no-daemon shadowJar

# Stage 4: Wolfi runtime. A shell and netcat are retained for diagnostics and health checks.
FROM cgr.dev/chainguard/wolfi-base:latest
RUN apk add --no-cache openjdk-17-jre netcat-openbsd
LABEL org.opencontainers.image.source="https://github.com/sarhatabaot/WatchWolfX"
WORKDIR /servers

# The mounted Docker socket is root-owned (0660) in the supported deployment. ServersManager
# must be able to create and clean up sibling Minecraft containers.
USER 0

# Copy pre-created directory structure
COPY --from=manager_build --chown=65532:65532 /out/ /servers/

# Copy the jar and plugin
COPY --from=manager_build --chown=65532:65532 \
    /src/manager/build/libs/watchwolfx-servermanager-*.jar \
    /servers/ServersManager.jar
COPY --from=plugin_build --chown=65532:65532 \
    /src/server/build/libs/WatchWolf-*-1.8-LATEST.jar \
    /servers/usual-plugins/

ENTRYPOINT ["/usr/lib/jvm/java-17-openjdk/bin/java"]
CMD ["-cp", "ServersManager.jar", "dev.watchwolf.serversmanager.ServersManager"]
