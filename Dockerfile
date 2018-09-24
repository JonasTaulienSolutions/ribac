FROM maven:3.5.4-jdk-10
EXPOSE 8080

WORKDIR /usr/ribac

COPY /src src
COPY /pom.xml pom.xml

RUN mvn clean package

ENTRYPOINT [                                                       \
    "java",                                                        \
    "-Djava.net.preferIPv4Stack=true",                             \
    "-jar", "target/right-based-access-control-0.1.0-SNAPSHOT.jar" \
]