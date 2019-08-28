

Master: [![Build Status](https://travis-ci.org/harvard-library/librarycloud_ingest.png?branch=master)](https://travis-ci.org/harvard-library/librarycloud_ingest)
Develop: [![Build Status](https://travis-ci.org/harvard-library/librarycloud_ingest.png?branch=develop)](https://travis-ci.org/harvard-library/librarycloud_ingest) 


The Library Cloud pipeline is an application to ingest metadata, transform, enrich and load for use by the Harvard Library Cloud Item api (v2). 
It is built using the Apache Camel Spring framework, and is meant to be used in conjunction to AWS sqs (amazon web services simple queueing service), though other queueing systems can be substituted).

To date, the app handles ingesting marc bibliographic records (in marc xml format), visual materials (VIA/JSTORForum), EAD (encoded archival description) archival finding aid component-level data, and Harvard TED collections.

## Requirements
* Maven
* Java 8
* Python
* AWS CLI (https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-install.html)
* AWS Tools (git clone https://github.com/harvard-library/aws-tools.git)

## Install:
1) Check out from github
2) copy ```src/main/resources/librarycloud.env.properties.example``` to
```src/main/resources/librarycloud.env.properties``` and add your specific properties
3) copy ```src/main/resources/META-INF/persistence.xml.example``` to
```src/main/resources/META-INF/persistence.xml``` [TO DO - not used, remove this dependency]
4) Run ```mvn -Dmaven.test.skip=true clean install``` (maven required) [TO DO - restore tests]
5) Setup AWS credentials. Enter access key, secret access key, and region when prompted.
   ```
   aws configure
   ```
6) Ensure AWS credentials are set in the environment, and the path includes necessary tools.
   ```
   export AWS_ACCESS_KEY_ID=<the access key>
   export AWS_SECRET_ACCESS_KEY=<the secret access key>
   export PATH=$PATH:~/aws-tools
   ```

## To run application (standalone, using maven):

* do ```mvn camel:run```
* In another terminal, run ingest script:
*./librarycloud/utils/ingest.sh [command] [source] [instance] [filepath/file]
* e.g.
```
./librarycloud/utils/ingest.sh ingest alma dev ./testmarc.xml
```


