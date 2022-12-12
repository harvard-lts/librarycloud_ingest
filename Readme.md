

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
- set `JAVA_HOME to openjdk 8`
- run `> mvn clean install`  
Tests should pass

## Docker build
- This is nonstandard and needs to be pulled into the standard docker framework at some point
> docker build . 

run 'docker images' to get the list of images and image IDs

> docker tag (image id) ingest-qa:[VERSION]  #e.g. 'docker tag 8bc41040fd9f ingest-qa:1.8.10'

> docker build  -f Dockerfile2 . 

run 'docker images' to get the list of images and image IDs

> docker tag (container id) lcingest-qa:[VERSION] #e.g. 'docker tag 8bc41040fd9f lcingest-qa:1.8.10'

log into docker registry 
docker login https://registry.lts.harvard.edu  
username:  
pass:  

# updating image: 
> docker tag lcingest-qa:[VERSION]registry.lts.harvard.edu/lts/lcingest-qa:latest

# upload the updated image
docker image push registry.lts.harvard.edu/lts/lcingest-qa:latest

MJV NOTES:
edit the Dockerfiles to the appropriate tag(branch) 


