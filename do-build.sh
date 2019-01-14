#!/usr/bin/env bash
export RIBAC_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)

echo "- Removing running ribac-db instance:"       \
  && docker-compose rm --stop --force ribac-db     \
                                                   \
  && echo "- Starting ribac-db:"                   \
  && docker-compose up --detach ribac-db           \
                                                   \
  && echo "- Wait until ribac-db has started:"     \
  && sleep 16s                                     \
                                                   \
  && echo "- Create docker image:"                 \
  && mvn package -Dmaven.test.skip=true            \
                                                   \
  && echo "- Removing running ribac-db instance:"  \
  && docker-compose rm --stop --force ribac-db