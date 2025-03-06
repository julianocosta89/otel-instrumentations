package io.temp.calculator;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class Thermometer {

    private static final Tracer tracer = GlobalOpenTelemetry.getTracer(Thermometer.class.getName(), "0.0.1");

    @Value("${thermometer.minTemp}")
    private int minTemp;

    @Value("${thermometer.maxTemp}")
    private int maxTemp;

    public int measureOnce() {
        Span span = tracer.spanBuilder("measureOnce")
                        .setSpanKind(SpanKind.INTERNAL)
                        .startSpan();

        try (Scope scope = span.makeCurrent()) {
            cpuIntensiveTask();
            span.setStatus(StatusCode.OK);
            return ThreadLocalRandom.current().nextInt(this.minTemp, this.maxTemp + 1);
        } finally {
            span.end();
        }
    }

    private void cpuIntensiveTask() {
        fibonacci(30);
    }

    private long fibonacci(int n) {
        if (n <= 1) return n;
        else return fibonacci(n - 1) + fibonacci(n - 2);
    }
}
