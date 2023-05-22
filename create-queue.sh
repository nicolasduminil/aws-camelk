#!/bin/bash
QUEUE_LIST=$(aws sqs list-queues --query "QueueUrls")
if [ '$QUEUE_LIST' = 'null' ]
then
  aws sqs create-queue --queue-name myQueue
else
  ./purge-sqs-queue.sh
fi