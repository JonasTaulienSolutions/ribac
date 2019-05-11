#!/usr/bin/env bash
export RIBAC_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)

./do-build.sh \
  && echo "- Stopping ribac:"                                 \
  && docker-compose rm --stop --force ribac                   \
                                                              \
  && echo "- Starting ribac:"                                 \
  && docker-compose up --detach ribac                         \
  && docker-compose logs --follow ribac                       \