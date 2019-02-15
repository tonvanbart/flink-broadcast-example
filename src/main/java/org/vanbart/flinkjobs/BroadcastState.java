package org.vanbart.flinkjobs;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.StateDescriptor;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.BroadcastStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
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

public class BroadcastState {

    private static final Logger log = LoggerFactory.getLogger(BroadcastState.class);

    public static final MapStateDescriptor<String, String> mapStateDescriptor =
            new MapStateDescriptor<String, String>("foo", BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO);

    /**
     * Main Flink job.
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        URL resource = Dataserver.class.getClassLoader().getResource("default-log4j.properties");
        PropertyConfigurator.configure(resource);

        // set up the streaming execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // uncomment following to explicitly set parallelism
//        env.setParallelism(1);

        ParameterTool paramTool = ParameterTool.fromArgs(args);
        env.getConfig().setGlobalJobParameters(paramTool);

        DataStreamSource<String> data = env.addSource(new SocketTextStreamFunction("localhost", 7777, " ", 3));

        DataStream<String> stringDataStreamSource = env.socketTextStream("localhost", 7778, " ", 3);
        // uncomment following line to have the elements in the stream printed
//        stringDataStreamSource.print();
        BroadcastStream<String> broadcast = stringDataStreamSource.broadcast(mapStateDescriptor);

        data.connect(broadcast)
                .process(new StatefulMultiply(1))
                .addSink(new PrintSinkFunction<>());

        env.execute("multiply");
    }

    /**
     * Stateful multiplication, factor taken from broadcasted state.
     */
    static class StatefulMultiply extends BroadcastProcessFunction<String, String, String> {

        public StatefulMultiply(Integer factor) {
            super();
            log.debug("StatefulMultiply({})", factor);
            this.factor = factor;
        }

        private static final Logger log = LoggerFactory.getLogger(StatefulMultiply.class);

        /** the multiply factor, initially 1. */
        private Integer factor = 1;

        @Override
        public void processElement(String value, ReadOnlyContext readOnlyContext, Collector<String> collector) throws Exception {
            log.debug("processElement({})", value);
            try {
                int number = Integer.parseInt(value);
                collector.collect(Integer.toString(factor * number));
            } catch (NumberFormatException e) {
                log.warn("processElement: could not parse '{}' to Integer, skipping element", value);
            }
        }

        @Override
        public void processBroadcastElement(String value, Context context, Collector<String> collector) throws Exception {
            log.debug("processBroadcastElement({})", value);
            try {
                factor = Integer.parseInt(value);
                log.debug("multiply factor set to {}", value);
            } catch (NumberFormatException e) {
                log.warn("Could not parse '{}' to Integer, state unchanged.", value);
            }
        }
    }

}
