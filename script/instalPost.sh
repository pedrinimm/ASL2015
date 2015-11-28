#!/bin/sh
sudo yum -y install postgresql94-server
sudo service postgresql-9.4 initdb
sudo chkconfig postgresql-9.4 on
sudo service postgresql-9.4 start