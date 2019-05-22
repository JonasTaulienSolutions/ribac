FROM openjdk:12
WORKDIR /usr/ribac
EXPOSE 8080
EXPOSE 5005

# Add Maven dependencies (not shaded into the artifact; Docker-cached)
ADD target/lib lib/

# Add the service itself
ADD target/right-based-access-control.jar ribac.jar

# Add logback-config
ADD logback.xml logback.xml

ENTRYPOINT [                                                                \
    "java",                                                                 \
    "-Djava.net.preferIPv4Stack=true",                                      \
    "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", \
    "-Dlogback.configurationFile=logback.xml",                              \
    "-jar", "ribac.jar"                                                     \
]