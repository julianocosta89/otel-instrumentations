FROM gradle:8.12.1-jdk21 AS builder
WORKDIR /usr/src/app/
COPY . .
RUN gradle build

FROM gcr.io/distroless/java21-debian12 AS runtime
WORKDIR /usr/src/app/
COPY --from=builder /usr/src/app/build/libs/tempcalculator-0.0.1.jar ./
ENTRYPOINT [ "java", "-jar", "tempcalculator-0.0.1.jar" ]
ARG version=2.13.0
ADD --chmod=644 https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v$version/opentelemetry-javaagent.jar /usr/src/app/opentelemetry-javaagent.jar
ENV JAVA_TOOL_OPTIONS=-javaagent:/usr/src/app/opentelemetry-javaagent.jar
