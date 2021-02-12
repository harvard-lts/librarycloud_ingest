

Master: [![Build Status](https://travis-ci.org/harvard-library/librarycloud_ingest.png?branch=master)](https://travis-ci.org/harvard-library/librarycloud_ingest)
Develop: [![Build Status](https://travis-ci.org/harvard-library/librarycloud_ingest.png?branch=develop)](https://travis-ci.org/harvard-library/librarycloud_ingest) 


The Library Cloud pipeline is an application to ingest metadata, transform, enrich and load for use by the Harvard Library Cloud Item api (v2). 
It is built using the Apache Camel Spring framework, and is meant to be used in conjunction to AWS sqs (amazon web services simple queueing service), though other queueing systems can be substituted).

To date, the app handles ingesting marc bibliographic records (in marc xml format), visual materials (VIA/JSTORForum), EAD (encoded archival description) archival finding aid component-level data, and Harvard TED collections.

## Install:
* Check out from github
* copy ```src/main/resources/librarycloud.env.properties.example``` to
```src/main/resources/librarycloud.env.properties``` and add your specific properties
* copy ```src/main/resources/META-INF/persistence.xml.example``` to
```src/main/resources/META-INF/persistence.xml.example``` [TO DO - not used, remove this dependency]
* Run ```mvn -Dmaven.test.skip=true clean install``` (maven required) [TO DO - restore tests]

## To run application (standalone, using maven):

* do mvn camel:run
* run ingest script:
*./librarycloud/utils/ingest.sh [command] [source] [instance] [filepath/file]
* e.g.
*./librarycloud/utils/ingest.sh ingest alma dev ./testmarc.xml


## Install instructions for Dockerized version - Dev only

Branch "dockerized_ingest" is currently for use in a local/dev environment, using docker-compose-local.yml 
(QA and Prod currently use separate docker workflow)

**External dependencies** (specific to Harvard University and not publicly accessible)  
This branch of librarycloud_ingest relies on:

* LTS dev S3 buckets and sqs/activemq queues
* Librarycloud item and collection api dev instance
* DRS Metadata Service dev instance

**Prerequisites**
* docker
* aws-cli
* (other dependencies - java, maven, etc - are within container)

**Install Steps**

* Checkout from github
```
git clone https://github.com/harvard-library/librarycloud_ingest.git
```
* copy```src/main/resources/librarycloud.env.properties.example``` to
```src/main/resources/librarycloud.env.properties``` and add your specific properties
* copy ```src/main/resources/META-INF/persistence.xml.example``` to
```src/main/resources/META-INF/persistence.xml.example``` [TO DO - not used, remove this dependency]

* for logging to file in local/dev env., overwrite ./src/main/resources/log4j.properties with log4j.properties-file in same dir


* Build docker container:
``` shellsession
$ docker-compose -f docker-compose-local.yml build
```
* Run the application in the local container:

``` shellsession
$ docker-compose -f docker-compose-local.yml up
```

**AWS Configuration**  
The ./utils/ingest.sh for ingest relies on aws-cli, and presence of dev profile at ~/.aws/config|credentials:
* config - example  
```
[default]  
region = us-east-1

[profile dev]  
region = us-east-1
```
* credentials example (note, not "profile dev" in credentials, just "dev"":
```
[default]  
aws_access_key_id = access_id
aws_secret_access_key = secret_key

[dev]
aws_access_key_id = access_id
aws_secret_access_key = secret_key
```

**IIIF bib metadata message generation** (Harvard only)

* Run this test ingest:
```
./util/ingest.sh ingest via dev src/test/resources/via_childs.xml
```
* Bring up activemq-prototype  
* Send this a message to local mds update queue via the UI:

Destination: 
```
Consumer.librarycloud-dev.VirtualTopic.DRS_OBJECT_UPDATED
```
Message Body:
```
{"data": {"objectId": "400338492"}}
```

This should drop a the following message to a new queue: iiifmetadata_update
```
{"drsObjectId","400338492"}
```

**TO DO for Dockerized install**

* Currently the Dockerfile.local checks out the code again, should not need to if already checked out
