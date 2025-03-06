package io.temp.calculator;

import io.opentelemetry.api.trace.Span;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CalculatorController {
    private static final Logger logger = LoggerFactory.getLogger(CalculatorController.class);

    @Autowired
    Thermometer thermometer;

    @GetMapping("/measureTemperature")
        public int measureTemperature(HttpServletRequest request) {
            Span span = Span.current();

            int temperature = thermometer.measureOnce();
            logger.info("Temperature measured: " + temperature);
            span.setAttribute("app.temp.measured", Integer.toString(temperature));

            return temperature;
        }
}
