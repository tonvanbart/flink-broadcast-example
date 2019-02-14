.PHONY: dataserver
dataserver:
	java -cp target/flink-broadcast-example-1.0.0-SNAPSHOT-jar-with-dependencies.jar org.vanbart.servers.Dataserver

.PHONY: stateserver
stateserver:
	java -cp target/flink-broadcast-example-1.0.0-SNAPSHOT-jar-with-dependencies.jar org.vanbart.servers.StateServer