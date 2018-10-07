FROM maven:3.5.4-jdk-10 as maven

ARG GEN_RIBAC_DB_HOST
ARG GEN_RIBAC_DB_EXTERNAL_PORT
ARG GEN_RIBAC_DB_NAME
ARG GEN_RIBAC_DB_USER
ARG GEN_RIBAC_DB_PASSWORD

COPY /pom.xml pom.xml
COPY /src src
RUN mvn package -Dmaven.test.skip=true                               \
        -DGEN_RIBAC_DB_HOST="${GEN_RIBAC_DB_HOST}"                   \
        -DGEN_RIBAC_DB_EXTERNAL_PORT="${GEN_RIBAC_DB_EXTERNAL_PORT}" \
        -DGEN_RIBAC_DB_NAME="${GEN_RIBAC_DB_NAME}"                   \
        -DGEN_RIBAC_DB_USER="${GEN_RIBAC_DB_USER}"                   \
        -DGEN_RIBAC_DB_PASSWORD="${GEN_RIBAC_DB_PASSWORD}"



FROM openjdk:10
WORKDIR /usr/ribac
EXPOSE 8080
EXPOSE 5005

COPY --from=maven target/right-based-access-control.jar right-based-access-control.jar

ENTRYPOINT [                                                                \
    "java",                                                                 \
    "-Djava.net.preferIPv4Stack=true",                                      \
    "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", \
    "-jar", "right-based-access-control.jar"                         \
]