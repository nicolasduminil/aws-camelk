#!/bin/sh
aws_camel_file_pod=$(kubectl get pods --namespace quarkus-camel | grep aws-camel-file | awk '{print $1}')
cat aws-camelk-model/src/main/resources/xml/money-transfers.xml | kubectl exec -i -n quarkus-camel $aws_camel_file_pod -- sh -c "cat > /tmp/input/money-transfers.xml"
