FROM ubuntu:14.04

RUN apt-get update && \
    apt-get -y install wget python

RUN sudo wget -O aerospike.tgz 'http://aerospike.com/download/server/latest/artifact/ubuntu12' && \
    tar -xvf aerospike.tgz && \
    cd aerospike-server-community-*-ubuntu12*  && \
    sudo ./asinstall # will install the .rpm packages  && \

EXPOSE 3000

CMD service aerospike start && tail
