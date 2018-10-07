#!/usr/bin/env bash
docker-compose rm --stop --force \
  && docker-compose up --detach ribac-db \
  && sleep 10s \
  && docker-compose up --detach --build ribac \
  && sleep 10s \
  && mvn test