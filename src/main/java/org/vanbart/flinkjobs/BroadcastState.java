package org.vanbart.flinkjobs;

import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.BroadcastStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.PrintSinkFunction;
import org.apache.flink.streaming.api.functions.source.SocketTextStreamFunction;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vanbart.servers.Dataserver;

import java.net.URL;
import java.util.Map;

public class BroadcastState {

    private static final Logger log = LoggerFactory.getLogger(BroadcastState.class);

    /**
     * Main Flink job.
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        URL resource = Dataserver.class.getClassLoader().getResource("default-log4j.properties");
        PropertyConfigurator.configure(resource);

        Map<String, Integer> defaultValue = StatefulMultiply.mapStateDescriptor.getDefaultValue();
        System.out.println("defaultValue = " + defaultValue);
        log.info("defaultValue = {}", defaultValue);

        // set up the streaming execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//        env.setParallelism(1);

        ParameterTool paramTool = ParameterTool.fromArgs(args);
        env.getConfig().setGlobalJobParameters(paramTool);

        DataStreamSource<String> data = env.addSource(new SocketTextStreamFunction("localhost", 7777, " ", 3));

        SocketTextStreamFunction stateSocket = new SocketTextStreamFunction("localhost", 7778, " ", 3);
        DataStreamSource<String> statesSource = env.addSource(new InitializingSourceFunction(stateSocket, "2"));

        BroadcastStream<String> broadcastedState = statesSource.broadcast(StatefulMultiply.mapStateDescriptor);

        data.connect(broadcastedState)
                .process(new StatefulMultiply())
                .addSink(new PrintSinkFunction<>());

        env.execute("multiply");
    }

}
