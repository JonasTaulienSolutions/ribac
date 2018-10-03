FROM maven:3.5.4-jdk-10
EXPOSE 8080

WORKDIR /usr/ribac

COPY /pom.xml pom.xml
RUN /usr/local/bin/mvn-entrypoint.sh mvn verify clean --fail-never

COPY /src src
ARG GEN_RIBAC_DB_HOST
ARG GEN_RIBAC_DB_EXTERNAL_PORT
ARG GEN_RIBAC_DB_NAME
ARG GEN_RIBAC_DB_USER
ARG GEN_RIBAC_DB_PASSWORD
RUN mvn -DGEN_RIBAC_DB_HOST="${GEN_RIBAC_DB_HOST}"                   \
        -DGEN_RIBAC_DB_EXTERNAL_PORT="${GEN_RIBAC_DB_EXTERNAL_PORT}" \
        -DGEN_RIBAC_DB_NAME="${GEN_RIBAC_DB_NAME}"                   \
        -DGEN_RIBAC_DB_USER="${GEN_RIBAC_DB_USER}"                   \
        -DGEN_RIBAC_DB_PASSWORD="${GEN_RIBAC_DB_PASSWORD}"           \
        -Dmaven.test.skip=true                                       \
        package


ENTRYPOINT [                                                       \
    "java",                                                        \
    "-Djava.net.preferIPv4Stack=true",                             \
    "-jar", "target/right-based-access-control.jar"                \
]