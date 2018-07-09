

Master: [![Build Status](https://travis-ci.org/harvard-library/librarycloud_ingest.png?branch=master)](https://travis-ci.org/harvard-library/librarycloud_ingest)
Develop: [![Build Status](https://travis-ci.org/harvard-library/librarycloud_ingest.png?branch=develop)](https://travis-ci.org/harvard-library/librarycloud_ingest) 

The Library Commons pipeline (alpha) is an application to ingest metadata, transform, enrich and load for use by the Harvard Library Cloud Item api (v2). 
It is built using the Apache Camel Spring framework, and is meant to be used in conjunction to AWS sqs (amazon web services simple queueing service), though other queueing systems can be substituted).

This application is currently under development. To date, the app handles ingesting marc bibliographic records (in marc21 communications format), converts to marcxml and then to mods. More steps for this particular workflow are being developed; additional workflows will be added to handle visual materials and EAD (encoded archival description) archival finding aid component-level data.

## Install:
* Check out from github
* For use with AWS, copy ```src/main/resources/aws.properties.example``` to
```src/main/resources/aws.properties``` and add your credentials
* Run ```mvn clean install``` (maven required)

## To run application (standalone, using maven):

* do mvn camel:run
* add the .mrc file to the directory specified in camel-context (default is /temp/aleph)
* add the corresponding .xml file to the same directory (sample message file is at src/test/resources/marcingest.xml)

## To ingest Aleph data 

Follow (these instructions)[https://github.com/harvard-library/librarycloud_deploy#deploy-a-control-server]

    

## On the way:

* Sample marcingest.mrc file to use for testing with marcingest.xml message
* More documentation;
* Configuration files and info for running in a servlet container (Tomcat, etc).;
* Additional steps for marc pipeline, and new pipelines for other data formats

