#!/usr/bin/env bash
export RIBAC_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)

./do-build.sh                                        \
 && IMAGE_NAME="rudolphcodes/ribac:${RIBAC_VERSION}" \
 && echo "- Push docker image ${IMAGE_NAME}:"        \
 && docker push "${IMAGE_NAME}"