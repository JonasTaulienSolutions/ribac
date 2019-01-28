#!/usr/bin/env bash
export RIBAC_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)

docker-compose up -d ribac-swagger

# Import variables from .env-File for $RIBAC_SWAGGER_EXTERNAL_PORT
export $(grep -v '^#' .env | xargs -0)

echo "Done: ribac swagger-ui at localhost:${RIBAC_SWAGGER_EXTERNAL_PORT}"