# Fabfile to:
#	 - compile the tools
#    - get parameters for experiment 
#    - copy applications to the remote servers 
#    - run applications
#    - retreive logs


# Import Fabric's API module
import time
from fabric.api import *



env.key_filename='./asl15.pem'

env.roledefs= {
	'middle':['ec2-user@52.17.102.181'],
	'client':['ec2-user@52.17.102.181'],
	'dryad01':['mpedro@dryad01'],
	'dryad08':['mpedro@dryad08'],
	'local':['pedrini@localhost'],
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


def installPostgresql():
	local('curl -O ftp://ftp.postgresql.org/pub/source/v9.4.4/postgresql-9.4.4.tar.bz2')
	put("./postgresql*","/home/mpedro")
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


def fullRunLocal(experimentID,dbServer,dbName,dbUser,dbPassword,noOfConnections,listeningPort,duration,serverPort,serverAddress,operationType,workload,noClients):
	# fab -R local fullRunLocal:experimentID=alpha,dbServer=localhost,dbName=messaging,dbUser=postgres,dbPassword=squirrel,noOfConnections=100,listeningPort=5432,duration=30,serverPort=5433,serverAddress=localhost,operationType=-1,workload=1,noClients=2
	#create log folder 
	local("mkdir logs_exp_{0}".format(experimentID))
	local("cd .. && ant clean")
	local("cd .. && ant Middleware")
	local("cd .. && ant Client")
	#run server
	local('screen java -jar ../dist/jar/Server-Messaging.jar {0} {1} {2} {3} {4} {5}'.format(dbServer,dbName,dbUser,dbPassword,noOfConnections,listeningPort))
	#run clients
	userName=""
	for i in range(int(noClients)):
		# print(i)
		userName="Client_{0}".format(i)
		local('screen -dmS {1} java -jar  ../dist/jar/client-Messaging.jar {0} {1} {2} {3} {4} {5}'.format(duration,userName,serverPort,serverAddress,operationType,workload))
	time.sleep(int(duration)+10)
	local('mv *.log logs_exp_{0}/'.format(experimentID))
	local('killall java')

