# Fabfile to:
#	 - compile the tools
#    - get parameters for experiment 
#    - copy applications to the remote servers 
#    - run applications
#    - retreive logs


# Import Fabric's API module
from fabric.api import *



env.key_filename='./asl15.pem'

env.roledefs= {
	'middle':['ec2-user@52.17.102.181'],
	'client':['ec2-user@52.17.102.181'],
	'dryad01':['mpedro@dryad01'],
	'dryad08':['mpedro@dryad08'],
	'dryad02':['mpedro@dryad02']
}

def createApplications(experimentID):
	#create log folder 
	local("mkdir logs_exp_%S"% experimentID)
	local("cd .. && ant clean")
	local("cd .. && ant Middleware")
	local("cd .. && ant Client")


def initMiddleware():
	
	put("../dist/jar/Server-Messaging.jar","/home/ec2-user")

def startMiddleware(dbServer,dbName,dbUser,dbPassword,noOfConnections,listeningPort):

	run('screen -S experiment')
	run('screen -S experiment -X java -jar Server-Messaging.jar %s %s %s %s %s %s %s' % dbServer,dbName,dbUser,db,dbPassword,noOfConnections,listeningPort)

def getMiddlewareLog(experimentID):
	local("mkdir ./logs_exp_%S/middleware"% experimentID)
	get(remote_path="./*.log", local_path="./logs_exp_%S/middleware"% experimentID)

def initClients():

	put("../dist/jar/client-Messaging.jar","/home/ec2-user")

def startClients(serverAddress,serverPort,noClients,operationType,duration):
	userName=""
	run('screen -S experiment')
	for i in range(1,noClients):
		userName="Client_%i" % i
		run('screen -S experiment -X java -jar client-Messaging.jar %s %s %s %s %s %s' % duration,userName,serverPort,serverAddress,operationType,duration)

def getClientLog(experimentID):
	local("mkdir ./logs_exp_%S/clients"% experimentID)
	get(remote_path="./*.log", local_path="./logs_exp_%S/clients"% experimentID)

def main():
	#compile application and jar creation
	createApplications()

	#init the middleware server
	initMiddleware()

	#init the clients
	# initClients(clientAddress,userName,pemPath)

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
	put("messaging","/mnt/local/mpedro/")
	run('/mnt/local/mpedro/bin/bin/createdb -p 1999 -h localhost -e messaging')

