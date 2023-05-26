#!/bin/sh
./delete-all-buckets.sh
./create-queue.sh
mvn -pl aws-camelk-file -Dquarkus.kubernetes.deploy=true -DskipTests clean package
mvn -pl aws-camelk-s3 -Dquarkus.kubernetes.deploy=true -DskipTests clean package
mvn -pl aws-camelk-jaxrs -Dquarkus.kubernetes.deploy=true -DskipTests clean package
mvn -pl aws-camelk-sqs -Dquarkus.kubernetes.deploy=true -DskipTests clean package
aws_camel_file_pod=$(kubectl get pods --namespace quarkus-camel | grep aws-camel-file | awk '{print $1}')
kubectl cp aws-camelk-model/src/main/resources/xml/money-transfers.xml quarkus-camel/$aws_camel_file_pod:/tmp/input

