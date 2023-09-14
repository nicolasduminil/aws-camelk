# Microservices with Camel Quarkus

This project shows four microservices implemented using Apache Camel on Quarkus,
as follows:

- ```aws-camelk-file```: this microservice is polling the ```/tmp/input``` local folder and, as soon as an XML file is comming, it store it in an AWS S3 bucket, which name starts with ```mys3``` followed by a random suffix.
- ```aws-camelk-s3```: this microservice is listening on the first found AWS S3 bucket which name starts with ```mys3``` and, as soon as an XML file comes in, it splits, tokenizes and streams it, before sending each message to an AWS SQS queue, which name is ```myQue```.
- ```aws-camelk-sqs```: this microservice subscribes for messages to the AWS SQS queue named ```myQueue``` and, for each incoming message, unmarshall it from XML to Java objects, the marshal it to JSON format, before sending it to the REST service below.
- ```aws-camelk-jaxrs```: this microservice exposes a REST API having endpoint for CRUDing money transfer orders. It consumes/produces JSON input/output data. It uses a service which exposes and interface defined by ```aws-camelk-api``` project. Several implementations of this interface might be present but, for simplicity sake, in the current case we're using the one defined by ```aws-camelk-provider``` project, named ```DefaultMoneyTransferProvider```, which only CRUds the money transfer order requests in an in-memory hash map.

## Deploying and running the microservices in Openshift

In order to deploy and run the miroservices in Openshift, proceed as follows:

### Login to Openshift

Here are the required steps to log in to Openshift Developer Sandbox:

- Fire your prefered browser and go to the Openshift Developer Sandbox site (https://developers.redhat.com/developer-sandbox)
- Click on the `Login` link in the upper right corner (you need to alrady have registered with the Openshift Developer Sandbox)
- Click on the red button labeled `Start your sandbox for free` in the center of the screen
- In the upper right corner, unfold your user name and click on the `Copy login command` button
- In the new dialog labeled `Log in with ...` click on the `DevSandbox` link
- A new page is displayed having a link labeled `Display Token`. Click on this link.
- Copy and execute the displayed oc command, for example 
    
      $ oc login --token=... --server=https://api.sandbox-m3.1530.p1.openshiftapps.com:6443

### Clone the project from GitHub

Here are the steps required to clone the project:

    $ git clone https://github.com/nicolasduminil/aws-camelk.git
    $ cd aws-camelk
    $ git checkout openshift

### Create the Openshift secret

Here are the steps required to create the Openshift secret:

- First encode your AWS access key ID and secret access key in Base64 as follows:

      $ echo -n <your AWS access key ID> | base64
      $ echo -n <your AWS secret access key> | base64

- Edit the file `aws-secret.yaml` and amend the following lines such that to replace `...` by the Base64 encoded values:

      AWS_ACCESS_KEY_ID: ...
      AWS_SECRET_ACCESS_KEY: ...

- Create he Openshift secret containing the AWS access key ID and secret access key:

      $ kubectl apply -f aws-secret.yaml

### Start the microservices

In order to start the microservices, run the following script:

    $ ./start-ms.sh

### Observe the log files

In order to follow the microservices execution run the commands below:

    $ oc get pods
    $ oc logs <pod-id>

### Stop the microservices

In order to stop the microservices, run the following script:

    $ ./kill-ms.sh

### Cleaning up the AWS infrastructure

In order to clean up the AWS infrastructure, run the commands below:

    $ ./delete-all-buckets.sh
    $ ./purge-sqs-queue.sh
    $ ./delete-sqs-queue.sh

### Log out from Openshift

In order to log out from Openshift unfold your user name in the right upper corner and select `Log out`.
