FROM gradle:8.12.1-jdk21 AS builder
WORKDIR /usr/src/app/
COPY . .
RUN gradle build

FROM gcr.io/distroless/java21-debian12
WORKDIR /usr/src/app/
COPY --from=builder /usr/src/app/build/libs/tempsimulator-0.0.1.jar ./
ENTRYPOINT [ "java", "-jar", "tempsimulator-0.0.1.jar" ]
