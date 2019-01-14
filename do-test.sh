#!/usr/bin/env bash
export RIBAC_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)

./do-build.sh \
  && docker-compose rm --stop --force \
  && docker-compose up --detach ribac-db \
  && docker-compose up --detach ribac \
  && echo "Restarted ribac and db. Starting tests in 16s..." \
  && sleep 16s \
  && mvn test \

docker-compose rm --stop --force