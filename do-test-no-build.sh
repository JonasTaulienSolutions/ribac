#!/usr/bin/env bash
docker-compose rm --stop --force \
  && docker-compose up --detach ribac-db \
  && docker-compose up --detach ribac \
  && sleep 10s \
  && mvn test