# Load-Balancer

This repo is a configurable load balancer written in java.
For now, only round robin algorithm has been implemented. For storage, in-memory storage has been implemented meaning that if we use more than one instances of this load balancer, they would not share the data with each other regarding the hosts to use for the api request.

## Features

### Algorithm
1. Round Robin - In this algorithm, hosts are forwarded incoming request in a sequence. When all the hosts are used, request are again forwarded from the first host that was used.

### Storage
1. In-Memory - Storage type is in-memory meaning the heap memory of the java server is used to store data about the hosts used. Using it in a distributed environment would mean that different instances of the load balancers would not share any data of the hosts they are redirecting the requests with each other. 

A new central storage type can be used and can be easily implemented by creating a class inside storage directory and implementing ILoadBalancerStorage interface.

### Health Check
There is future present which continuously checks for health check of the given urls on a provided health check endpoints. If a server is down, the load balancer will not redirect request to it until it is healthy again.

### How to use

You can use the jar present in the target folder to run the load balancer server with the below given command.

``java -jar -DApp.config.file=application.properties load-balancer-0.0.1-SNAPSHOT.jar ``

Example of application properties can be found inside resources directory.

``print.machine.update.info=true`` This can be turned true to debug. It will give details abut the machines being added and removed from the list of available machines.

``print.machines.available.info`` Use this to print the available machines after every health check.

``health.check.time.interval`` This can be used to input the time after which health of the machines should be checked. It is in milliseconds.