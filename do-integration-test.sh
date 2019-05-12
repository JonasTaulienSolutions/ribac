#!/usr/bin/env bash
source helper-functions.sh

exec 3< <(docker-compose logs --follow ribac)               \
&& echo "- Execute tests:"                                  \
&& mvn test -Djooq.codegen.skip=true -Dmaven.main.skip      \

success=$?

if [ ${success} -ne 0 ]; then
    echo "- Ribac logs:"
    cat <&3
    echo "FAILURE! ^^^^^^^^^^ See ribac logs above ^^^^^^^^^^"

else
    echo "SUCCESS!"
fi