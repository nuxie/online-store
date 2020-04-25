FROM ubuntu:19.10

MAINTAINER Zuzanna Kalinowska <zkalinowska96@gmail.com>

ENV SCALA_VERSION 2.12.8
ENV JAVA_VERSION 8
ENV NPM_VERSION 6.8
ENV USER zkalinowska

RUN apt-get update && \
    apt-get install -y vim git unzip wget curl \
                       openjdk-$JAVA_VERSION-jdk \
		       npm && \
    wget www.scala-lang.org/files/archive/scala-$SCALA_VERSION.deb && \
    dpkg -i scala-$SCALA_VERSION.deb

RUN npm install -g npm@$NPM_VERSION

RUN echo "deb https://dl.bintray.com/sbt/debian /" | tee -a /etc/apt/sources.list.d/sbt.list && \
    curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" | apt-key add && \
    apt-get update && \
    apt-get install sbt        

EXPOSE 8000 9000 5000 8888

RUN useradd --create-home --shell /bin/bash $USER && \
    apt-get install -y sudo && \
    echo "$USER ALL=(root) NOPASSWD:ALL" > /etc/sudoers.d/$USER && \
    chmod 0440 /etc/sudoers.d/$USER

USER $USER

WORKDIR /home/$USER/project
VOLUME ["/home/$USER/projekt"]

