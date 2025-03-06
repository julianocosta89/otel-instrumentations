package io.temp.simulator;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class TemperatureController {
    private static final Logger logger = LoggerFactory.getLogger(TemperatureController.class);

    @Autowired
    Thermometer thermometer;

    private final Tracer tracer;

    @Autowired
    TemperatureController(OpenTelemetry openTelemetry) {
        this.tracer = openTelemetry.getTracer(TemperatureController.class.getName(), "0.0.1");
    }

    @GetMapping("/simulateTemperature")
    public List<Integer> simulateTemperature(@RequestParam("location") Optional<String> location,
                                            @RequestParam("measurements") Optional<Integer> measurements) {

        Span span = tracer.spanBuilder("GET /simulateTemperature")
                        .setSpanKind(SpanKind.SERVER)
                        .startSpan();

        try (Scope scope = span.makeCurrent()) {

            if (measurements.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing measurements parameter", null);
            }

            List<Integer> result = thermometer.process(measurements.get());

            if (location.isPresent()) {
                logger.info("Temperature simulation for {}: {}", location.get(), result);
                span.setAttribute("app.location", location.get());
            } else {
                logger.info("Temperature simulation for an unspecified location: {}", result);
            }

            span.setStatus(StatusCode.OK);
            return result;
        } catch (Throwable t) {
            span.setStatus(StatusCode.ERROR);
            span.recordException(t);
            throw t;
        } finally {
            span.end();
        }
    }
}
