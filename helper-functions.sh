#!/usr/bin/env bash
export RIBAC_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
export JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-14.0.2/Contents/Home

db_exec(){
  command="${1}"
  docker-compose exec -T ribac-db sh -c "exec mysql -uroot -hlocalhost -p\"\${MYSQL_ROOT_PASSWORD}\" -e \"${command}\""
}
export -f db_exec




db_exec_script(){
  databaseName="${1}"
  scriptName="${2}"
  docker-compose exec -T ribac-db sh -c "exec mysql -uroot -hlocalhost -p\"\${MYSQL_ROOT_PASSWORD}\" -D\"${databaseName}\"" < "${scriptName}"
}
export -f db_exec_script




db_create_backup(){
  databaseName="${1}"
  backupName="${2}"
  docker-compose exec -T ribac-db sh -c "exec mysqldump -uroot -p\"\${MYSQL_ROOT_PASSWORD}\" \"${databaseName}\"" > "./backups/${backupName}"
}
export -f db_create_backup




db_restore_backup(){
  databaseName="${1}"
  backupName="${2}"
  db_exec_script "${databaseName}" "./backups/${backupName}"
}
export -f db_restore_backup




db_stop(){
  echo "- Stopping ribac-db:" && docker-compose rm --stop --force ribac-db
}
export -f db_stop




db_start(){
  echo "- Starting ribac-db:" && docker-compose up --detach ribac-db
  success=$?

  if [[ "${success}" = 0 ]]; then
      for i in {30..0}; do
          if db_exec 'SELECT 1;' &> /dev/null; then
              break
          fi
          echo 'MySQL init process in progress...'
          sleep 1
      done

      if [[ "$i" = 0 ]]; then
          echo >&2 'MySQL init process failed.'
          return 1
      else
          return 0
      fi
  else
      return "${success}"
  fi
}
export -f db_start




generate(){
   echo "- Generating model classes and jooq DSL:" && mvn clean compile
}
export -f generate




image_create(){
  echo "- Create docker image:" && mvn clean package -Dmaven.test.skip=true
}
export -f image_create




image_push(){
 IMAGE_NAME="jonastauliensolutions/ribac:${RIBAC_VERSION}"

 echo "- Push docker image ${IMAGE_NAME}:" && docker push "${IMAGE_NAME}"
}
export -f image_push




ribac_start(){
  echo "- Starting ribac ${RIBAC_VERSION}:" && docker-compose up --detach ribac
}
export -f ribac_start




ribac_stop(){
  echo "- Stopping ribac:" && docker-compose rm --stop --force ribac
}
export -f ribac_stop





swagger_start(){
  docker-compose up -d ribac-swagger

  # Import variables from .env-File for $RIBAC_SWAGGER_EXTERNAL_PORT
  export $(grep -v '^#' .env | xargs -0)

  echo "Done: ribac swagger-ui at localhost:${RIBAC_SWAGGER_EXTERNAL_PORT}"
}
export -f swagger_start
