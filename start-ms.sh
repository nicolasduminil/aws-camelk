#!/bin/sh
./delete-all-buckets.sh
./create-queue.sh
./aws-camelk-file/target/aws-camelk-file-runner &
sleep 3
./aws-camelk-s3/target/aws-camelk-s3-runner &
sleep 3
./aws-camelk-jaxrs/target/aws-camelk-jaxrs-runner &
sleep 3
./aws-camelk-sqs/target/aws-camelk-sqs-runner &
sleep 3
ps ef | grep -i aws-camelk-file | grep -v grep | awk {'print $1'} > pid-aws-camelk-file.pid
ps ef | grep -i aws-camelk-s3 | grep -v grep | awk {'print $1'} > pid-aws-camelk-s3.pid
ps ef | grep -i aws-camelk-jaxrs | grep -v grep | awk {'print $1'} > pid-aws-camelk-jaxrs.pid
ps ef | grep -i aws-camelk-sqs | grep -v grep | awk {'print $1'} > pid-aws-camelk-sqs.pid
./copy-xml-file.sh

