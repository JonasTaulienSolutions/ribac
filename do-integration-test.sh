#!/usr/bin/env bash
source helper-functions.sh

   echo "- Execute tests:"                                  \
&& mvn test -Djooq.codegen.skip=true -Dmaven.main.skip      \

success=$?

if [ ${success} -ne 0 ]; then
    echo "FAILURE! ^^^^^^^^^^ See ribac logs above ^^^^^^^^^^"
else
    echo "SUCCESS!"
fi