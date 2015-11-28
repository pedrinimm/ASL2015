#!/bin/sh
###############################
#
# Read command line arguments
#
###############################

function usage() {
	local programName=$1
	echo "Usage: $programName --serverMachine=<address> --clientMachine=<address> --noOfClients=<int> --remoteUserName=<username> --experimentId=<id> --clientRunTime=<seconds>"
	exit -1
}

serverMachine=""
clientMachine=""
noOfClients=""
remoteUserName=""
experimentId=""

clientRunTime=5

# Extract command line arguments
TEMP=`getopt -o b: --long serverMachine:,clientMachine:,noOfClients:,remoteUserName:,experimentId:,clientRunTime: \
     -n 'example.bash' -- "$@"`

if [ $? != 0 ] ; then echo "Terminating..." >&2 ; exit 1 ; fi

# Note the quotes around `$TEMP': they are essential!
eval set -- "$TEMP"

while true ; do
        case "$1" in
                --serverMachine) serverMachine="$2" ; shift 2 ;;
                --clientMachine) clientMachine="$2" ; shift 2 ;;
                --noOfClients) noOfClients="$2" ; shift 2 ;;
                --remoteUserName) remoteUserName="$2" ; shift 2 ;;
                --experimentId) experimentId="$2" ; shift 2 ;;
                --clientRunTime) clientRunTime="$2" ; shift 2 ;;
                --) shift ; break ;;
                *) echo "Internal error!" ; exit 1 ;;
        esac
done

# Check for correctness of the commandline arguments
if [[ $serverMachine == "" || $clientMachine == "" || $noOfClients == "" || $remoteUserName == "" || $experimentId == "" ]]
then
	usage $1
fi

#####################################
#
# Copy server and clients to machines
#
#####################################

echo -ne "  Testing passwordless connection to the server machine and client machine... "
# Check if command can be run on server and client
success=$( ssh -i asl15.pem -o BatchMode=yes  $remoteUserName@$serverMachine echo ok 2>&1 )
if [ $success != "ok" ]
then
	echo "Passwordless login not successful for $remoteUserName on $serverMachine. Exiting..."
	exit -1
fi

success=$( ssh -i asl15.pem -o BatchMode=yes  $remoteUserName@$clientMachine echo ok 2>&1 )
if [ $success != "ok" ]
then
	echo "Passwordless login not successful for $remoteUserName on $clientMachine. Exiting..."
	exit -1
fi
echo "OK"

# echo "  Copying server.jar to server machine: $serverMachine ... "
# # Copy jar to server machine
# scp CodeJar/AslTest.jar $remoteUserName@$serverMachine:/tmp
# echo "  Copying client.jar to client machine: $serverMachine ... "
# # Copy jar to client machine
# scp CodeJar/AslTest.jar $remoteUserName@$clientMachine:/tmp