# ubuntu 
FROM ubuntu:latest as builder

RUN apt-get update && \
    apt-get -y install openjdk-17-jdk-headless git && \
    apt-get -y install maven && \
    mkdir -p /home/librarycloud/ingest/cameldata && \
    cd  /home/librarycloud/ && git clone https://github.com/harvard-library/librarycloud_ingest.git && \
    cd /home/librarycloud/librarycloud_ingest && git checkout LTSMAINT-840 && \
    useradd --uid 55003 -m lcadm
WORKDIR /home/librarycloud/librarycloud_ingest
COPY --chown=lcadm ./ .
RUN chown lcadm:lcadm -R /home/librarycloud/librarycloud_ingest

USER lcadm

RUN mvn clean install -Dmaven.test.skip=true 

CMD mvn camel:run -Dmaven.test.skip=true 
