# Fabfile to:
#	 - compile the tools
#    - get parameters for experiment 
#    - copy applications to the remote servers 
#    - run applications
#    - retreive logs


# Import Fabric's API module
import time
from fabric.api import *
import sys
import matplotlib.pyplot as plt
import numpy as np
import math
from os import listdir
from os.path import isfile, join
import csv 
import codecs
import copy

env.key_filename='./asl15.pem'

env.roledefs= {
	'middle':['ec2-user@52.17.102.181'],
	'client':['ec2-user@52.17.102.181'],
	'dryad01':['mpedro@dryad01'],
	'dryad08':['mpedro@dryad08'],
	'local':['pedrini@localhost'],
	'dbAmazon':['ubuntu@52.30.249.149'],
	'midAmazon':['ubuntu@52.30.231.160'],
	'cliAmazon':['ubuntu@52.30.190.106'],
	'midAmazon2':['ubuntu@52.19.10.233'],
	'cliAmazon2':['ubuntu@52.17.52.80'],
	'dryad02':['mpedro@dryad02']
}


def createApplications(experimentID):
	#create log folder 
	local("mkdir logs_exp_%S"% experimentID)
	local("cd .. && ant clean")
	local("cd .. && ant Middleware")
	local("cd .. && ant Client")


def initMiddleware():
	# fab -R dryad02 initMiddleware
	put("../../Middleware","/home/mpedro")
	run('mkdir /mnt/local/mpedro')
	run('mv Middleware/ /mnt/local/mpedro')

def startMiddleware(dbServer,dbName,dbUser,dbPassword,noOfConnections,listeningPort):
	# fab -R dryad02 startMiddleware:dbServer=dryad08,dbName=messaging,dbUser=mpedro,dbPassword=squirrel,noOfConnections=100,listeningPort=1999
	# run('screen -S experiment')
	run('cd /mnt/local/mpedro/Middleware && ant Middleware')
	run('mkdir /mnt/local/mpedro/running')
	run('mv /mnt/local/mpedro/Middleware/dist/jar/Server-Messaging.jar /mnt/local/mpedro/running/')
	run('screen java -jar /mnt/local/mpedro/running/Server-Messaging.jar {0} {1} {2} {3} {4} {5}'.format(dbServer,dbName,dbUser,dbPassword,noOfConnections,listeningPort))

def getMiddlewareLog(experimentID):
	local("mkdir ./logs_exp_%S/middleware"% experimentID)
	get(remote_path="./*.log", local_path="./logs_exp_%S/middleware"% experimentID)

def initClients():
	# fab -R dryad01 initClients
	put("../../Middleware","/home/mpedro")
	run('mkdir /mnt/local/mpedro')
	run('mv Middleware/ /mnt/local/mpedro')
	

def startClients(duration,serverPort,serverAddress,operationType,workload,noClients):
	# fab -R dryad01 startClients:duration=30,serverPort=1999,serverAddress=dryad02,operationType=-1,workload=1,noClients=2
	run('cd /mnt/local/mpedro/Middleware && ant Client')
	run('mkdir /mnt/local/mpedro/running')
	run('mv /mnt/local/mpedro/Middleware/dist/jar/client-Messaging.jar /mnt/local/mpedro/running/')
	userName=""
	for i in range(1,int(noClients)):
		userName="Client_{0}".format(i)
		run('screen java -jar  /mnt/local/mpedro/running/client-Messaging.jar {0} {1} {2} {3} {4} {5}'.format(duration,userName,serverPort,serverAddress,operationType,workload))

def getClientLog(experimentID):
	local("mkdir ./logs_exp_%S/clients"% experimentID)
	get(remote_path="./*.log", local_path="./logs_exp_%S/clients"% experimentID)

def fullAmazon(experimentID,dbServer,dbName,dbUser,dbPassword,noOfConnections,listeningPort,duration,serverPort,serverAddress,serverAddress2,operationType,workload,noClients):
	# fab -R local fullAmazon:experimentID=stability_1,dbServer=52.30.249.149,dbName=messaging,dbUser=postgres,dbPassword=squirrel,noOfConnections=30,listeningPort=5432,duration=30,serverPort=5433,serverAddress=52.30.231.160,serverAddress2=52.19.10.233,operationType=5,workload=0.25,noClients=15
	# local("mkdir logs_exp_{0}".format(experimentID))

	# local("fab -R local installDBAmazon")

	# local("fab -R midAmazon intsallBasicTools")
	# local("fab -R cliAmazon intsallBasicTools")
	# local("fab -R midAmazon2 intsallBasicTools")
	# local("fab -R cliAmazon2 intsallBasicTools")

	local("fab -R midAmazon moveCompileMiddleAmazon:dbServer={0},dbName={1},dbUser={2},dbPassword={3},noOfConnections={4},listeningPort={5}".format(dbServer,dbName,dbUser,dbPassword,noOfConnections,listeningPort))
	local("fab -R midAmazon2 moveCompileMiddleAmazon:dbServer={0},dbName={1},dbUser={2},dbPassword={3},noOfConnections={4},listeningPort={5}".format(dbServer,dbName,dbUser,dbPassword,noOfConnections,listeningPort))
	local("fab -R cliAmazon moveCompileClientsAmazon:duration={0},serverPort={1},serverAddress={2},operationType={3},workload={4},noClients={5}".format(duration,serverPort,serverAddress,operationType,workload,noClients))
	local("fab -R cliAmazon2 moveCompileClientsAmazon:duration={0},serverPort={1},serverAddress={2},operationType={3},workload={4},noClients={5}".format(duration,serverPort,serverAddress2,operationType,workload,noClients))
	# wait for a cliens to be done
	waitTime=math.ceil(float(noClients)/float(noOfConnections))*(float(duration)+40.0)
	print(waitTime)
	time.sleep(waitTime)
	local("mkdir logs_exp_{0}".format(experimentID))
	local("mkdir logs_exp_{0}_1".format(experimentID))
	local("fab -R midAmazon getLogsMidAmaz:destination=logs_exp_{0}".format(experimentID))
	local("fab -R midAmazon2 getLogsMidAmaz:destination=logs_exp_{0}_1".format(experimentID))
	local("fab -R cliAmazon getLogsCliAmaz:destination=logs_exp_{0}".format(experimentID))
	local("fab -R cliAmazon2 getLogsCliAmaz:destination=logs_exp_{0}_1".format(experimentID))

def getLogsMidAmaz(destination):
	get(remote_path="./*.log", local_path="{0}/".format(destination))
	run('killall java')
	run('rm *.log')

def getLogsCliAmaz(destination):
	get(remote_path="./*.log", local_path="{0}/".format(destination))
	run('rm *.log')
	

def moveCompileMiddleAmazon(dbServer,dbName,dbUser,dbPassword,noOfConnections,listeningPort):
	# put("../../Middleware","/home/ubuntu")
	# run("cd Middleware && ant clean")
	# run("cd Middleware && ant Middleware")
	run('dtach -n `mktemp -u /tmp/detach.XXX` java -Dlog4j.configurationFile=/home/ubuntu/Middleware/src/log4j2.xml -jar ./Middleware/dist/jar/Server-Messaging.jar {0} {1} {2} {3} {4} {5}'.format(dbServer,dbName,dbUser,dbPassword,noOfConnections,listeningPort))

def moveCompileClientsAmazon(duration,serverPort,serverAddress,operationType,workload,noClients):
	# put("../../Middleware","/home/ubuntu")
	# run("cd Middleware && ant clean")
	# run("cd Middleware && ant Client")
	#run clients
	userName=""
	for i in range(int(noClients)):
		# print(i)
		userName="Client_{0}".format(i)
		run('dtach -n `mktemp -u /tmp/{1}.XXX` java -Dlog4j.configurationFile=/home/ubuntu/Middleware/src/log4j2.xml -jar  ./Middleware/dist/jar/client-Messaging.jar {0} {1} {2} {3} {4} {5}'.format(duration,userName,serverPort,serverAddress,operationType,workload))



def installDBAmazon():
	# fab -R local installDBAmazon
	local("fab -R dbAmazon intsallBasicTools")
	local("fab -R dbAmazon installPostgresql2")
	# local(echo "after this modify the files in the postgres server and restart")
	# local(echo "sudo nano /etc/postgresql/9.3/main/pg_hba.conf")
	# local(echo "sudo nano /etc/postgresql/9.3/main/postgresql.conf")
	# local(echo "sudo service postgresql restart")

def intsallBasicTools():
	sudo('apt-get update')
	sudo('apt-get install build-essential')
	sudo('apt-get install default-jdk')
	sudo('apt-get install flex bison ant dtach')
	

def installPostgresql2():
	sudo('apt-get install postgresql postgresql-contrib')
	put("messaging.tar","/home/ubuntu/")
	sudo('psql postgres', user="postgres")
	sudo('createdb -h localhost -e messaging',user="postgres")
	sudo('pg_restore -F tar -d messaging /home/ubuntu/messaging.tar',user="postgres")

def installPostgresql():
	local('curl -O ftp://ftp.postgresql.org/pub/source/v9.4.4/postgresql-9.4.4.tar.bz2')
	put("./postgresql*","/home/ubuntu")
	# run('screen -S database')
	run('tar xvjf postgresql*')
	run('mkdir /mnt/local/mpedro')
	run('mkdir /mnt/local/mpedro/bin')
	run('mv postgresql-9.4.4 /mnt/local/mpedro')
	run('cd /mnt/local/mpedro/postgresql-9.4.4 && ./configure --prefix="/mnt/local/mpedro/bin/"')
	run('cd /mnt/local/mpedro/postgresql-9.4.4 && make && make install')
	run('LD_LIBRARY_PATH=/mnt/local/mpedro/bin/lib')
	run('export LD_LIBRARY_PATH')
	run('PATH=/mnt/local/mpedro/bin/bin:$PATH')
	run('export PATH')
	run('mkdir /mnt/local/mpedro/bin/data')
	#optional
	# run('adduser postgres')
	# run('passwd postgres')
	# run('chown postgres:postgres /mnt/local/mpedro/bin/data')
	# run('ls -ld /mnt/local/mpedro/bin/data')
	# run('su - postgres')
	#endoptional
	
	# run('/mnt/local/mpedro/bin/bin/initdb -D /mnt/local/mpedro/bin/data/')
	run('export LC_CTYPE=en_US.UTF-8')
	run('/mnt/local/mpedro/bin/bin/initdb -D /mnt/local/mpedro/bin/data/ -E UTF8 -A md5 -W')
	# run('screen -S runDB')
	run('screen /mnt/local/mpedro/bin/bin/postgres -D /mnt/local/mpedro/bin/data -h * -p 1999 -i -k /mnt/local/mpedro -N 100')
	put("messaging.tar","/mnt/local/mpedro/")
	run('/mnt/local/mpedro/bin/bin/createdb -p 1999 -h localhost -e messaging')
	run('/mnt/local/mpedro/bin/bin/pg_restore -F tar -d messaging /mnt/local/mpedro/messaging.tar')


def fullRunLocal(experimentID,dbServer,dbName,dbUser,dbPassword,noOfConnections,listeningPort,duration,serverPort,serverAddress,operationType,workload,noClients,messageType):
	# fab -R local fullRunLocal:experimentID=alpha,dbServer=localhost,dbName=messaging,dbUser=postgres,dbPassword=squirrel,noOfConnections=100,listeningPort=5432,duration=30,serverPort=5433,serverAddress=localhost,operationType=-1,workload=1,noClients=2,messageType=0
	#create log folder 
	local("mkdir logs_exp_{0}".format(experimentID))
	local("cd .. && ant clean")
	local("cd .. && ant Middleware")
	local("cd .. && ant Client")
	#run server
	local('screen java -Dlog4j.configurationFile=../src/log4j2.xml -jar ../dist/jar/Server-Messaging.jar {0} {1} {2} {3} {4} {5}'.format(dbServer,dbName,dbUser,dbPassword,noOfConnections,listeningPort))
	#run clients
	userName=""
	for i in range(int(noClients)):
		# print(i)
		userName="Client_{0}".format(i)
		local('screen -dmS {1} java -Dlog4j.configurationFile=../src/log4j2.xml -jar  ../dist/jar/client-Messaging.jar {0} {1} {2} {3} {4} {5} {6}'.format(duration,userName,serverPort,serverAddress,operationType,workload,messageType))
	waitTime=math.ceil(float(noClients)/float(noOfConnections))*(float(duration)+40.0)
	print(waitTime)
	time.sleep(waitTime)
	local('mv *.log logs_exp_{0}/'.format(experimentID))
	local('killall java')
	# local('rm logs_exp_{0}/.DS_Store'.format(experimentID))

def parsing(pathOfLogs):
	# fab -R local parsing:pathOfLogs=/Users/pedrini/Documents/workspace/Middleware/script/logs_exp_alpha/
	handler="ClientHandler"
	# this is for store the results of the clients
	clients=[]
	# this is for store the result from the client handlers
	middleware=[]
	# this is to store the timing for the database
	database=[]
	# all throughput
	throughput=[]
	for f in listdir(pathOfLogs):
		with open(pathOfLogs+f,'rU') as fo:
			# this if check wheather is a log from a handler or a client
			if f.find(handler)!=-1:
				print("Log from server")
				# individual handler operations
				cHandler=[]
				# individual database time
				cDatabase=[]
				spamreader = csv.reader(fo, delimiter='\t',)
				time=""
				time2=""
				# this is for line reading control
				name=""
				name2=""
				controller=1
				controller2=1
				operation=""
				operation2=""
				# individual throughput per handler
				iThroughput=[]
				for row in spamreader:
					
					# first we check if the line in the file has enough parameters
					if len(row)>2:
						
						# this if check if the line I am reading is from logging the db or regular 
						# print(controller)
						# print(row)
						if row[1] == "db":	
							# print(row)
							if controller == 1 and name <>  row[0]:
								# print(" name is {2} operation is {0} and row[3] is {1}".format(operation,row[3],row[0]))
								name=row[0]
								operation=row[3]
								time=row[4]
								controller=0
								# print(operation,time,controller)
							else:
								# print(" name is {2} operation is {0} and row[3] is {1}".format(operation,row[3],row[0]))
								if operation == row[3] and name <>  row[0]: 
									# print("True")
									name=row[0]
									cDatabase.append([row[2],row[3],time,row[4],int(row[4])-int(time)])
									controller=1
								# print(operation,time,controller)
						else:
							# print(row)
							if controller2 == 1 and name <>  row[0]:
								# print(" name is {2} operation is {0} and row[3] is {1}".format(operation2,row[2],row[0]))
								name2=row[0]
								operation2=row[2]
								time2=row[3]
								controller2=0
								# print(operation2,time2,controller2)
							else:
								# print(" name is {2} operation is {0} and row[3] is {1}".format(operation2,row[2],row[0]))
								if operation2 == row[2] and name2<>row[0]:
									# print("True")
									name2=row[0]
									cHandler.append([row[1],row[2],time2,row[3],int(row[3])-int(time2)])
									controller2=1
								# print(operation2,time2,controller2)
					else:
						# print(row[0])
						if row[0].find('_throughput')!=-1:
							# print(row[1])
							iThroughput.append(row[1])


				# print(iThroughput)
				print("request")
				print(len(cHandler))
				print("database")
				print(len(cDatabase))
				print("throughput")
				print(len(iThroughput))
				middleware.append(cHandler)
				database.append(cDatabase)
				throughput.append(iThroughput)
				# del cHandler[:]
				# del cDatabase[:]
				# for line in fo:
				
			else:
				print("Log from client or middleware")
				client=[]
				spamreader = csv.reader(fo, delimiter='\t',)
				# spamreader = csv.reader((line.replace('\0','') for line in fo), delimiter="\t")
				time=""
				# # this is for line reading control
				controller=1
				operation=""
				for row in spamreader:
					# print(row)
					# first we check if the line in the file has enough parameters
					if len(row)>3:
						# print(row)
						if controller == 1:
							# print("operation is {0} and row[2] is {1}".format(operation,row[2]))
							operation=row[2]
							time=row[3]
							controller=0
							# print(operation2,time2,controller2)
						else:
							# print("operation is {0} and row[2] is {1}".format(operation,row[2]))
							if operation == row[2]:
								# print("HERE")
								client.append([row[1],row[2],time,row[3],int(row[3])-int(time)])
								controller=1
				print(len(client))
				clients.append(client)
				# del client[:]
	

	print("Done parsing")
	plt.figure(1)
	plt.hold(True)
	plt.suptitle('Clients')
	plt.ylabel('Response Time (ms)', fontsize=14)
	plt.xlabel('# of Request', fontsize=14)
	# plt.ylim((0,600))
	for item in clients:
		x=[]
		y=[]
		for measure in item:
			x.append(measure[4])
			y.append(measure[1])
		plt.plot(x)

	plt.figure(2)
	plt.hold(True)
	plt.suptitle('Middleware')
	plt.ylabel('Response Time (ms)', fontsize=14)
	plt.xlabel('# of Request', fontsize=14)
	# plt.ylim((0,600))
	for item in middleware:
		x=[]
		y=[]

		for measure in item:
			x.append(measure[4])
			y.append(measure[1])
		plt.plot(x)
	
	plt.figure(3)
	plt.hold(True)
	plt.suptitle('Database')
	plt.ylabel('Response Time (ms)', fontsize=14)
	plt.xlabel('# of Request', fontsize=14)
	# plt.ylim((0,600))
	for item in database:
		x=[]
		y=[]

		for measure in item:
			x.append(measure[4])
			y.append(measure)
		plt.plot(x)

	plt.figure(4)
	plt.hold(True)
	plt.suptitle('Middleware Handlers Individual Throughput')
	plt.ylabel('Throughput (Request/Second)', fontsize=14)
	plt.xlabel('Seconds', fontsize=14)
	# plt.ylim((0,600))
	c=[]
	a=c
	b=[]
	for item in throughput:
		item= map(int, item)
		# print(len(item))
		b=item
		if len(a) < len(b):
			c = copy.copy(b)
			c = map(int, c)
			for i in range(len(a)):
				c[i] = c[i]+a[i]
		else:
			c = copy.copy(a)
			c = map(int, c)
			for i in range(len(b)):
				c[i] = c[i]+b[i]
			# c[:len(b)] += b
		a=[]
		a=c
		a = map(int, a)
		c=[]
	# for item in throughput:
	# 	x=[]
	# 	y=[]
	# 	count=0
	# 	y.append(0)
	# 	for indThr in item:
	# 		count=count+1
	# 		y.append(indThr)
	# 	x=range(count+1)
	# 	plt.plot(y)
	print("Final size of a {0}".format(len(a)))
	plt.plot(a)
	plt.show()
	
	
	



			