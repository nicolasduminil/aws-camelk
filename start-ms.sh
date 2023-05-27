#!/bin/sh
./delete-all-buckets.sh
./create-queue.sh
mvn -DskipTests -Dquarkus.kubernetes.deploy clean package
sleep 3
./copy-xml-file.sh
