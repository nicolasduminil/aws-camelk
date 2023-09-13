#!/bin/sh
./delete-all-buckets.sh
./create-queue.sh
sleep 10
#mvn -DskipTests -Dquarkus.kubernetes.deploy -Dquarkus.container-image.builder=jib -Dquarkus.openshift.build-strategy=docker -Dkubernetes.deployment.target=openshift clean package
mvn -DskipTests -Dquarkus.kubernetes.deploy clean package
sleep 3
./copy-xml-file.sh
