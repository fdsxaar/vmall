From getintodevops/jenkins-withdocker:lts-docker18.06.0
LABEL maintainer="fdsxaar"
ENV REFRESHED_AT 2020-12-4

#Install kubectl on Ubuntu
RUN sudo apt-get update && sudo apt-get install -y apt-transport-https gnupg2 curl
RUN curl -s https://packages.cloud.google.com/apt/doc/apt-key.gpg | sudo apt-key add -
RUN echo "deb https://apt.kubernetes.io/ kubernetes-xenial main" | sudo tee -a /etc/apt/sources.list.d/kubernetes.list
RUN sudo apt-get update
RUN sudo apt-get install -y kubectl
