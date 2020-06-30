package org.vanbart.flinkjobs;

import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.co.BroadcastProcessFunction;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Stateful multiplication, factor taken from broadcasted state.
 */
class StatefulMultiply extends BroadcastProcessFunction<String, String, String> {

    public static final MapStateDescriptor<String, Integer> mapStateDescriptor =
            new MapStateDescriptor<>("multiplicationFactor", BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.INT_TYPE_INFO);

    private List<Integer> bufferedValues;

    public StatefulMultiply() {
        super();
        log.debug("StatefulMultiply()");
        this.bufferedValues = new ArrayList<>();
    }

    private static final Logger log = LoggerFactory.getLogger(StatefulMultiply.class);

    @Override
    public void processElement(String value, ReadOnlyContext readOnlyContext, Collector<String> collector) throws Exception {
        log.debug("processElement({})", value);
        try {
            int number = Integer.parseInt(value);
            Integer factor = readOnlyContext.getBroadcastState(mapStateDescriptor).get("value");
            if (factor == null) {
                // need to do buffering here
                log.debug("\n\nBuffering value: {}\n\n", number);
                bufferedValues.add(number);
            } else {
                log.debug("Checking buffer");
                if (!bufferedValues.isEmpty()) {
                    for (Integer nr : bufferedValues) {
                        log.debug("Handling buffered value: {}", nr);
                        collector.collect(Integer.toString(factor * nr));
                    }
                    bufferedValues.clear();
                }
                collector.collect(Integer.toString(factor * number));
            }
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
