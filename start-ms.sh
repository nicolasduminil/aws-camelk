#!/bin/sh
./delete-all-buckets.sh
./create-queue.sh
sleep 10
mvn -DskipTests -Dquarkus.kubernetes.deploy clean package
sleep 3
./copy-xml-file.sh
