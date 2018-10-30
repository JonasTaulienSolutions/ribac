#!/usr/bin/env bash
docker-compose rm --stop --force \
  && docker-compose up --detach ribac-db \
  && docker-compose up --detach ribac \
  && echo "Restarted ribac and db. Starting tests in 10s..." \
  && sleep 10s \
  && mvn test