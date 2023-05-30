#!/bin/bash
REGION=$(aws configure get region)
QUEUE_LIST=$(aws sqs list-queues --queue-name-prefix myQueue --region $REGION --query "QueueUrls[0]" --output text)
if [ $QUEUE_LIST = None ]
then
  aws sqs create-queue --queue-name myQueue --region $REGION
else
  aws sqs purge-queue --queue-url $QUEUE_LIST --region $REGION
fi
