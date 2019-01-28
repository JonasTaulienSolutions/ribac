#!/usr/bin/env bash
export RIBAC_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)

./do-build.sh \
  && echo "- Stopping ribac:"                                 \
  && docker-compose rm --stop --force ribac                   \
                                                              \
  && echo "- Starting ribac:"                                 \
  && docker-compose up --detach ribac                         \
  && exec 3< <(docker-compose logs --follow ribac)            \
                                                              \
  && echo "- Execute tests:"                                  \
  && mvn test -Djooq.codegen.skip=true -Dmaven.main.skip      \

success=$?

echo "- Stopping ribac and ribac-db:"
docker-compose rm --stop --force ribac ribac-db

if [ ${success} -ne 0 ]; then
    echo "- Ribac logs:"
    cat <&3
    echo "FAILURE! ^^^^^^^^^^ See ribac logs above ^^^^^^^^^^"

else
    echo "SUCCESS!"
fi