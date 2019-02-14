package org.vanbart.flinkjobs;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.PrintSinkFunction;
import org.apache.flink.streaming.api.functions.source.SocketTextStreamFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BroadcastState {

    private static final Logger log = LoggerFactory.getLogger(BroadcastState.class);

    /**
     * Main Flink job.
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        // set up the streaming execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // uncomment following to explicitly set parallelism
//        env.setParallelism(1);

        ParameterTool paramTool = ParameterTool.fromArgs(args);
        env.getConfig().setGlobalJobParameters(paramTool);

//        TypeInformation<String> stringTypeInformation = new TypeHint<String>() {} .getTypeInfo();

        DataStreamSource<String> localhost = env.addSource(new SocketTextStreamFunction("localhost", 7777, " ", 3));
        localhost.map(value -> value)
//        localhost.map(identityMapper)
//                .returns(stringTypeInformation)
                .addSink(new PrintSinkFunction<>());
        env.execute("rechtdoor");
    }

    static MapFunction<String, String> identityMapper = new MapFunction<String, String>() {
        @Override
        public String map(String value) throws Exception {
            return value;
        }
    };
}
