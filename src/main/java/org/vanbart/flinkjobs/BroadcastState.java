package org.vanbart.flinkjobs;

import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.BroadcastStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.BroadcastProcessFunction;
import org.apache.flink.streaming.api.functions.sink.PrintSinkFunction;
import org.apache.flink.streaming.api.functions.source.SocketTextStreamFunction;
import org.apache.flink.util.Collector;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vanbart.servers.Dataserver;

import java.net.URL;
import java.util.Map;

public class BroadcastState {

    private static final Logger log = LoggerFactory.getLogger(BroadcastState.class);

    public static final MapStateDescriptor<String, Integer> mapStateDescriptor =
            new MapStateDescriptor<>("multiplicationFactor", BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.INT_TYPE_INFO);

    /**
     * Main Flink job.
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        URL resource = Dataserver.class.getClassLoader().getResource("default-log4j.properties");
        PropertyConfigurator.configure(resource);

        Map<String, Integer> defaultValue = mapStateDescriptor.getDefaultValue();
        System.out.println("defaultValue = " + defaultValue);
        log.info("defaultValue = {}", defaultValue);

        // set up the streaming execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//        env.setParallelism(1);

        ParameterTool paramTool = ParameterTool.fromArgs(args);
        env.getConfig().setGlobalJobParameters(paramTool);

        DataStreamSource<String> data = env.addSource(new SocketTextStreamFunction("localhost", 7777, " ", 3));

        SocketTextStreamFunction stateSocket = new SocketTextStreamFunction("localhost", 7778, " ", 3);
        InitializingSourceFunction states = new InitializingSourceFunction(stateSocket, "2");
        DataStreamSource<String> statesSource = env.addSource(states);

//        stringDataStreamSource.print();
        BroadcastStream<String> broadcastedState = statesSource.broadcast(mapStateDescriptor);

        data.connect(broadcastedState)
                .process(new StatefulMultiply())
                .addSink(new PrintSinkFunction<>());

        env.execute("multiply");
    }

    /**
     * Stateful multiplication, factor taken from broadcasted state.
     */
    static class StatefulMultiply extends BroadcastProcessFunction<String, String, String> {

        public StatefulMultiply() {
            super();
            log.debug("StatefulMultiply()");
        }

        private static final Logger log = LoggerFactory.getLogger(StatefulMultiply.class);

        @Override
        public void processElement(String value, ReadOnlyContext readOnlyContext, Collector<String> collector) throws Exception {
            log.debug("processElement({})", value);
            try {
                int number = Integer.parseInt(value);
                Integer factor = readOnlyContext.getBroadcastState(mapStateDescriptor).get("value");
                if (factor == null) {
                    log.warn("\nDid not find a broadcast state, defaulting to 1!\n");
                    factor = 1;
                }
                collector.collect(Integer.toString(factor * number));
            } catch (NumberFormatException e) {
                log.warn("processElement: could not parse '{}' to Integer, skipping element", value);
            }
        }

        @Override
        public void processBroadcastElement(String value, Context context, Collector<String> collector) throws Exception {
            log.debug("processBroadcastElement({})", value);
            try {
                int factor = Integer.parseInt(value);
                log.debug("multiply factor set to {}", value);
                context.getBroadcastState(mapStateDescriptor).put("value", factor);
            } catch (NumberFormatException e) {
                log.warn("Could not parse '{}' to Integer, state unchanged.", value);
            }
        }

        @Override
        public void open(Configuration configuration) {
        }
    }
}
