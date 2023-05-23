#!/bin/sh
./delete-all-buckets.sh
./create-queue.sh
java -jar ./aws-camelk-file/target/quarkus-app/quarkus-run.jar &
sleep 3
java -jar ./aws-camelk-s3/target/quarkus-app/quarkus-run.jar &
sleep 3
./copy-xml-file.sh
sleep 3
java -jar ./aws-camelk-jaxrs/target/quarkus-app/quarkus-run.jar &
sleep 3
java -jar ./aws-camelk-sqs/target/quarkus-app/quarkus-run.jar &
sleep 3
ps ef | grep -i aws-camelk-file | grep -v grep | awk {'print $1'} > pid-aws-camelk-file.pid
ps ef | grep -i aws-camelk-s3 | grep -v grep | awk {'print $1'} > pid-aws-camelk-s3.pid
#ps ef | grep -i aws-camelk-jaxrs | grep -v grep | awk {'print $1'} > pid-aws-camelk-jaxrs.pid
ps ef | grep -i aws-camelk-sqs | grep -v grep | awk {'print $1'} > pid-aws-camelk-sqs.pid
