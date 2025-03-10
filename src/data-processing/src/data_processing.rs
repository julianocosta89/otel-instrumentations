use actix_web::{get, Responder};
use actix_web_opentelemetry::ClientExt;
use opentelemetry::global;
use log::{info, warn};
use rand::Rng;
use std::env;
use std::io;

#[get("/processTemperature")]
pub async fn process_temp() -> impl Responder {
    if do_processing().await {
        let resp = get_temperature().await.unwrap();
        return format!("{resp}");
    }

    warn!("Error processing temperature");
    format!("Temperature error")
}

async fn do_processing() -> bool {
    let meter = global::meter("processing_time_meter");
    let processing_time = meter.f64_histogram("app.processing_time").build();
    let start = std::time::Instant::now();

    let mut rng = rand::rng();
    tokio::time::sleep(tokio::time::Duration::from_millis(rng.random_range(0..100))).await;

    processing_time.record(start.elapsed().as_secs_f64(), &[]);
    true
}

async fn get_temperature() -> io::Result<String> {
    let client = awc::Client::new();
    let temp_calculator_addr = format!(
        "{}{}",
        env::var("TEMP_CALCULATOR_ADDR").unwrap(),
        "/measureTemperature"
    );

    info!("Requesting temperature from: {}", temp_calculator_addr);

    let mut response = client
        .get(temp_calculator_addr)
        .trace_request()
        .send()
        .await
        .map_err(|err| io::Error::new(io::ErrorKind::Other, err.to_string()))?;

    let bytes = response
        .body()
        .await
        .map_err(|err| io::Error::new(io::ErrorKind::Other, err.to_string()))?;

    std::str::from_utf8(&bytes)
        .map(|s| s.to_owned())
        .map_err(|err| io::Error::new(io::ErrorKind::Other, err))
}
