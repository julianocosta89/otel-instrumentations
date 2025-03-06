package io.temp.simulator;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.TextMapSetter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Component
public class Thermometer {
    @Autowired
    private RestTemplate restTemplate;

    private final Tracer tracer;
    private final Meter meter;
    private LongCounter measurementCounter;

    @Autowired
    Thermometer(OpenTelemetry openTelemetry) {
        tracer = openTelemetry.getTracer(Thermometer.class.getName(), "0.1.0");
        meter = openTelemetry.getMeter(Thermometer.class.getName());
        this.measurementCounter = meter.counterBuilder("app.measurement.counter").build();
    }

    private String url = System.getenv("DATA_PROCESSING_URL");

    public List<Integer> process(int measurements) {
        Span span = tracer.spanBuilder("process")
                        .setSpanKind(SpanKind.CLIENT)
                        .startSpan();

        List<Integer> temperatures = new ArrayList<Integer>();
        measurementCounter.add(measurements);
        try (Scope scope = span.makeCurrent()) {
            for (int i = 0; i < measurements; i++) {
                HttpHeaders headers = new HttpHeaders();
                TextMapSetter<HttpHeaders> setter = HttpHeaders::set;
                GlobalOpenTelemetry.getPropagators().getTextMapPropagator().inject(Context.current(), headers, setter);

                HttpEntity<String> entity = new HttpEntity<>(headers);
                ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
                int temperature = Integer.valueOf(response.getBody());

                temperatures.add(temperature);
            }
            span.setStatus(StatusCode.OK);
            return temperatures;
        } finally {
            span.end();
        }
    }
}
