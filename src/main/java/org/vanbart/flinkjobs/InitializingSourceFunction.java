package org.vanbart.flinkjobs;

import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InitializingSourceFunction implements SourceFunction<String> {

    private SourceFunction<String> delegate;

    private String initialValue;

    private static final Logger log = LoggerFactory.getLogger(InitializingSourceFunction.class);

    public InitializingSourceFunction(SourceFunction<String> delegate, String initialValue) {
        log.debug("InitializingSourceFunction({},{})", delegate, initialValue);
        this.delegate = delegate;
        this.initialValue = initialValue;
    }

    @Override
    public void run(SourceContext<String> ctx) throws Exception {
        log.debug("run({})", ctx);
        ctx.collect(initialValue);
        delegate.run(ctx);
    }

    @Override
    public void cancel() {
        log.debug("cancel()");
        delegate.cancel();
    }
}
