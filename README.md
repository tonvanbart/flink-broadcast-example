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
The output also will show you which Flink process handled the update; this is dependent on the number of cores in
your system (in my case 8):

    /Users/ton/.jenv/versions/oracle64-1.8.0.152/bin/java "-javaagent:/Applications/IntelliJ IDEA CE.app/Contents/lib/idea_rt.jar=62351:/Applications/IntelliJ IDEA CE.app/Contents/bin" -Dfile.encoding=UTF-8 -classpath /Users/ton/.jenv/versions/oracle64-1.8.0.152/jre/lib/charsets.jar:/Users/ton/.jenv/versions/oracle64-1.8.0.152/jre/lib/deploy.jar:/Users/ton/.jenv/versions/oracle64-1.8.0.152/jre/lib/ext/cldrdata.jar:/Users/ton/.jenv/versions/oracle64-1.8.0.152/jre/lib/ext/dnsns.jar:/Users/ton/.jenv/versions/oracle64-1.8.0.152/jre/lib/ext/jaccess.jar:/Users/ton/.jenv/versions/oracle64-1.8.0.152/jre/lib/ext/jfxrt.jar:/Users/ton/.jenv/versions/oracle64-1.8.0.152/jre/lib/ext/localedata.jar:/Users/ton/.jenv/versions/oracle64-1.8.0.152/jre/lib/ext/nashorn.jar:/Users/ton/.jenv/versions/oracle64-1.8.0.152/jre/lib/ext/sunec.jar:/Users/ton/.jenv/versions/oracle64-1.8.0.152/jre/lib/ext/sunjce_provider.jar:/Users/ton/.jenv/versions/oracle64-1.8.0.152/jre/lib/ext/sunpkcs11.jar:/Users/ton/.jenv/versions/oracle64-1.8.0.152/jre/lib/ext/zipfs.jar:/Users/ton/.jenv/versions/oracle64-1.8.0.152/jre/lib/javaws.jar:/Users/ton/.jenv/versions/oracle64-1.8.0.152/jre/lib/jce.jar:/Users/ton/.jenv/versions/oracle64-1.8.0.152/jre/lib/jfr.jar:/Users/ton/.jenv/versions/oracle64-1.8.0.152/jre/lib/jfxswt.jar:/Users/ton/.jenv/versions/oracle64-1.8.0.152/jre/lib/jsse.jar:/Users/ton/.jenv/versions/oracle64-1.8.0.152/jre/lib/management-agent.jar:/Users/ton/.jenv/versions/oracle64-1.8.0.152/jre/lib/plugin.jar:/Users/ton/.jenv/versions/oracle64-1.8.0.152/jre/lib/resources.jar:/Users/ton/.jenv/versions/oracle64-1.8.0.152/jre/lib/rt.jar:/Users/ton/.jenv/versions/oracle64-1.8.0.152/lib/ant-javafx.jar:/Users/ton/.jenv/versions/oracle64-1.8.0.152/lib/dt.jar:/Users/ton/.jenv/versions/oracle64-1.8.0.152/lib/javafx-mx.jar:/Users/ton/.jenv/versions/oracle64-1.8.0.152/lib/jconsole.jar:/Users/ton/.jenv/versions/oracle64-1.8.0.152/lib/packager.jar:/Users/ton/.jenv/versions/oracle64-1.8.0.152/lib/sa-jdi.jar:/Users/ton/.jenv/versions/oracle64-1.8.0.152/lib/tools.jar:/Users/ton/github/tonvanbart/flink-broadcast-example/target/classes:/Users/ton/.m2/repository/org/slf4j/slf4j-api/1.7.25/slf4j-api-1.7.25.jar:/Users/ton/.m2/repository/org/slf4j/slf4j-log4j12/1.7.21/slf4j-log4j12-1.7.21.jar:/Users/ton/.m2/repository/log4j/log4j/1.2.17/log4j-1.2.17.jar:/Users/ton/.m2/repository/org/apache/flink/flink-streaming-java_2.11/1.7.0/flink-streaming-java_2.11-1.7.0.jar:/Users/ton/.m2/repository/org/apache/flink/flink-runtime_2.11/1.7.0/flink-runtime_2.11-1.7.0.jar:/Users/ton/.m2/repository/org/apache/flink/flink-queryable-state-client-java_2.11/1.7.0/flink-queryable-state-client-java_2.11-1.7.0.jar:/Users/ton/.m2/repository/org/apache/flink/flink-hadoop-fs/1.7.0/flink-hadoop-fs-1.7.0.jar:/Users/ton/.m2/repository/commons-io/commons-io/2.4/commons-io-2.4.jar:/Users/ton/.m2/repository/org/apache/flink/flink-shaded-netty/4.1.24.Final-5.0/flink-shaded-netty-4.1.24.Final-5.0.jar:/Users/ton/.m2/repository/org/apache/flink/flink-shaded-asm/5.0.4-5.0/flink-shaded-asm-5.0.4-5.0.jar:/Users/ton/.m2/repository/org/apache/flink/flink-shaded-jackson/2.7.9-5.0/flink-shaded-jackson-2.7.9-5.0.jar:/Users/ton/.m2/repository/org/apache/commons/commons-lang3/3.3.2/commons-lang3-3.3.2.jar:/Users/ton/.m2/repository/org/javassist/javassist/3.19.0-GA/javassist-3.19.0-GA.jar:/Users/ton/.m2/repository/org/scala-lang/scala-library/2.11.12/scala-library-2.11.12.jar:/Users/ton/.m2/repository/com/typesafe/akka/akka-actor_2.11/2.4.20/akka-actor_2.11-2.4.20.jar:/Users/ton/.m2/repository/com/typesafe/config/1.3.0/config-1.3.0.jar:/Users/ton/.m2/repository/org/scala-lang/modules/scala-java8-compat_2.11/0.7.0/scala-java8-compat_2.11-0.7.0.jar:/Users/ton/.m2/repository/com/typesafe/akka/akka-stream_2.11/2.4.20/akka-stream_2.11-2.4.20.jar:/Users/ton/.m2/repository/org/reactivestreams/reactive-streams/1.0.0/reactive-streams-1.0.0.jar:/Users/ton/.m2/repository/com/typesafe/ssl-config-core_2.11/0.2.1/ssl-config-core_2.11-0.2.1.jar:/Users/ton/.m2/repository/org/scala-lang/modules/scala-parser-combinators_2.11/1.0.4/scala-parser-combinators_2.11-1.0.4.jar:/Users/ton/.m2/repository/com/typesafe/akka/akka-protobuf_2.11/2.4.20/akka-protobuf_2.11-2.4.20.jar:/Users/ton/.m2/repository/com/typesafe/akka/akka-slf4j_2.11/2.4.20/akka-slf4j_2.11-2.4.20.jar:/Users/ton/.m2/repository/org/clapper/grizzled-slf4j_2.11/1.3.2/grizzled-slf4j_2.11-1.3.2.jar:/Users/ton/.m2/repository/com/github/scopt/scopt_2.11/3.5.0/scopt_2.11-3.5.0.jar:/Users/ton/.m2/repository/org/xerial/snappy/snappy-java/1.1.4/snappy-java-1.1.4.jar:/Users/ton/.m2/repository/com/twitter/chill_2.11/0.7.6/chill_2.11-0.7.6.jar:/Users/ton/.m2/repository/com/twitter/chill-java/0.7.6/chill-java-0.7.6.jar:/Users/ton/.m2/repository/org/apache/flink/flink-shaded-guava/18.0-5.0/flink-shaded-guava-18.0-5.0.jar:/Users/ton/.m2/repository/org/apache/commons/commons-math3/3.5/commons-math3-3.5.jar:/Users/ton/.m2/repository/com/google/code/findbugs/jsr305/1.3.9/jsr305-1.3.9.jar:/Users/ton/.m2/repository/org/apache/flink/force-shading/1.7.0/force-shading-1.7.0.jar:/Users/ton/.m2/repository/org/apache/flink/flink-clients_2.11/1.7.0/flink-clients_2.11-1.7.0.jar:/Users/ton/.m2/repository/org/apache/flink/flink-core/1.7.0/flink-core-1.7.0.jar:/Users/ton/.m2/repository/org/apache/flink/flink-annotations/1.7.0/flink-annotations-1.7.0.jar:/Users/ton/.m2/repository/org/apache/flink/flink-metrics-core/1.7.0/flink-metrics-core-1.7.0.jar:/Users/ton/.m2/repository/com/esotericsoftware/kryo/kryo/2.24.0/kryo-2.24.0.jar:/Users/ton/.m2/repository/com/esotericsoftware/minlog/minlog/1.2/minlog-1.2.jar:/Users/ton/.m2/repository/org/objenesis/objenesis/2.1/objenesis-2.1.jar:/Users/ton/.m2/repository/commons-collections/commons-collections/3.2.2/commons-collections-3.2.2.jar:/Users/ton/.m2/repository/org/apache/commons/commons-compress/1.4.1/commons-compress-1.4.1.jar:/Users/ton/.m2/repository/org/tukaani/xz/1.0/xz-1.0.jar:/Users/ton/.m2/repository/org/apache/flink/flink-optimizer_2.11/1.7.0/flink-optimizer_2.11-1.7.0.jar:/Users/ton/.m2/repository/org/apache/flink/flink-java/1.7.0/flink-java-1.7.0.jar:/Users/ton/.m2/repository/commons-cli/commons-cli/1.3.1/commons-cli-1.3.1.jar org.vanbart.flinkjobs.BroadcastState
    defaultValue = null
    7> 4
    8> 5
    1> 3
    2> 5
    3> 2
    4> 60
    5> 60
    6> 30
    7> 10
    8> 60
    1> 30
    
You can see where I set the mutplication factor to 10, from the (hardcoded) default 1.

Hit `Ctrl-C` to terminate the data and state servers.