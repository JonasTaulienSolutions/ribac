FROM maven:3.5.4-jdk-10
EXPOSE 8080

WORKDIR /usr/ribac

COPY /pom.xml pom.xml
RUN /usr/local/bin/mvn-entrypoint.sh mvn verify clean --fail-never

COPY /src src
RUN mvn package

ENTRYPOINT [                                                       \
    "java",                                                        \
    "-Djava.net.preferIPv4Stack=true",                             \
    "-jar", "target/right-based-access-control.jar"                \
]