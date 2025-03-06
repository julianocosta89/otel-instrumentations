FROM rust:1.84 AS builder
WORKDIR /app/
COPY . .
RUN cargo build -r

FROM debian:bookworm-slim AS release
WORKDIR /app
COPY --from=builder /app/target/release/dataprocessing /app/dataprocessing
EXPOSE 9000
ENTRYPOINT ["/app/dataprocessing"]
