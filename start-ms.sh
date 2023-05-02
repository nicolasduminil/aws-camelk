#!/bin/sh
java -jar ./aws-camelk-file/target/quarkus-app/quarkus-run.jar &
sleep 3
#java -jar ./aws-camelk-s3/target/quarkus-app/quarkus-run.jar &
ps ef | grep -i aws-camelk-file | grep -v grep | awk {'print $1'} > pid-aws-camelk-file.pid
#ps ef | grep -i aws-camelk-s3 | grep -v grep | awk {'print $1'} > pid-aws-camelk-s3.pid
