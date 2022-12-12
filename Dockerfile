# ubuntu 
FROM ubuntu:latest

RUN apt-get update
RUN apt-get -y install openjdk-8-jdk-headless git
RUN apt-get -y install maven
RUN mkdir -p /home/librarycloud/ingest/cameldata 
RUN cd  /home/librarycloud/ && git clone https://github.com/harvard-library/librarycloud_ingest.git 
RUN cd /home/librarycloud/librarycloud_ingest && git checkout LTSCLOUD-1125
#ADD persistence.xml /home/librarycloud/librarycloud_ingest/src/main/resources/META-INF/persistence.xml
#RUN useradd --create-home lcadm
WORKDIR /home/librarycloud/librarycloud_ingest
COPY --chown=lcadm ./ .
RUN useradd --uid 55003 -m lcadm
USER lcadm
#RUN mkdir -p /home/librarycloud/librarycloud_ingest/target/classes

RUN mvn -e -X clean install -Dmaven.test.skip=true 
