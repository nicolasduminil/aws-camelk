#!/bin/sh
aws_camel_file_pod=$(oc get pods | grep aws-camel-file | grep -wv -e build -e deploy | awk '{print $1}')
cat aws-camelk-model/src/main/resources/xml/money-transfers.xml | oc exec -i $aws_camel_file_pod -- sh -c "cat > /tmp/input/money-transfers.xml"
