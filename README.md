## Flink broadcast state example

This is a work in progress.

### Classes
Class `Dataserver` will listen for connections on port 7777 and will send a random number 1-6 to any connected sockets
twice per second.
<br>
Class `StateServer` will listen for connections on port 7778 and allow input of an integer on `stdin`. Any number entered will
be echoed to all connected sockets.
<br>
`BroadcastState` will connect to both sockets, and will multiply the numbers received on port 7777 with the multiplication factor
received from port 7778.

### Running
Build a fat jar from the sources:

    mvn clean package

Open two terminals to start both the servers and observe their logging:

    make dataserver
    make stateserver
    
(If your system does not have the `make` tool then see the [Makefile](Makefile) for the commands to use.)

You can run the Flink job by running `BroadcastState` from within your IDE. This should start an embedded mini Flink 
cluster and show you the log; since the job is using `PrintSinkFunction` the output of the pipeline is in the log.
Type a multiplication factor into the terminal running the state server to see the update being handled.