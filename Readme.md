

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

## Test
- set `JAVA_HOME to openjdk 17`
- run `> mvn clean install`  
Tests should pass

## Docker build and deploy
Modify the Dockerfile to contain the correct tag or branch  
- (... git checkout <TAG_OR_BRANCH>)
Build the container
- docker compose -f docker-compose.yml build
Push to the repository (need to be logged in)
- docker image push registry.lts.harvard.edu/lts/lcingest:latest
Stop/Start container on qa (legacy dev not yet ready on cloud, so we start wit qa)

## Run docker locally
Make sure Dockerfile has correct branch you want to pull and test
- docker-compose -f docker-compose.yml up --build -d --force-recreate




