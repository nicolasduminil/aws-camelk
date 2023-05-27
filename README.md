# Microservices with Camel Quarkus

This project shows four microservices implemented using Apache Camel on Quarkus,
as follows:

- ```aws-camelk-file```: this microservice is polling the ```/tmp/input``` local folder and, as soon as an XML file is comming, it store it in an AWS S3 bucket, which name starts with ```mys3``` followed by a random suffix.
- ```aws-camelk-s3```: this microservice is listening on the first found AWS S3 bucket which name starts with ```mys3``` and, as soon as an XML file comes in, it splits, tokenizes and streams it, before sending each message to an AWS SQS queue, which name is ```myQue```.
- ```aws-camelk-sqs```: this microservice subscribes for messages to the AWS SQS queue named ```myQueue``` and, for each incoming message, unmarshall it from XML to Java objects, the marshal it to JSON format, before sending it to the REST service below.
- ```aws-camelk-jaxrs```: this microservice exposes a REST API having endpoint for CRUDing money transfer orders. It consumes/produces JSON input/output data. It uses a service which exposes and interface defined by ```aws-camelk-api``` project. Several implementations of this interface might be present but, for simplicity sake, in the current case we're using the one defined by ```aws-camelk-provider``` project, named ```DefaultMoneyTransferProvider```, which only CRUds the money transfer order requests in an in-memory hash map.

## Deploying and running locally the microservices

In order to deploy and run the miroservices locally, proceed as follows:

### Clone the project from GitHub

Here are the steps required to clone the project:

    $ git clone https://github.com/nicolasduminil/aws-camelk.git
    $ cd aws-camelk

### Start the microservices

In order to start the microservices, run the following script:

    $ ./start-ms.sh

### Stop the microservices

In order to stop the microservices, run the following script:

    $ ./kill-ms.sh

### Cleaning up the AWS infrastructure

In order to clean up the AWS infrastructure, run the commands below:

    $ ./delete-all-buckets.sh
    $ ./purge-sqs-queue.sh
    $ ./delete-sqs-queue.sh
