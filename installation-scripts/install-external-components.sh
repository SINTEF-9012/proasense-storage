#!/bin/bash
sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 7F0CEB10
echo "deb http://repo.mongodb.org/apt/ubuntu "$(lsb_release -sc)"/mongodb-org/3.0 multiverse" | sudo tee /etc/apt/sources.list.d/mongodb-org-3.0.list
sudo apt-get update
sudo apt-get --yes --force-yes install default-jdk
sudo apt-get --yes --force-yes install nano
sudo apt-get --yes --force-yes install maven
sudo apt-get --yes --force-yes remove maven2
sudo apt-get --yes --force-yes install tomcat7
sudo apt-get --yes --force-yes install tomcat7-admin
sudo apt-get --yes --force-yes install -y mongodb-org
