#!/bin/bash
sudo cp config/tomcat-users.xml /etc/tomcat7/tomcat-users.xml
sudo service tomcat7 restart
sudo cp config/mongod.conf /etc/mongod.conf
sudo service mongod restart
