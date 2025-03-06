use actix_web::{App, HttpServer};
use actix_web_opentelemetry::RequestTracing;

mod telemetry_conf;
use telemetry_conf::init_otel;

use log::{info, warn};

mod data_processing;
use data_processing::process_temp;

#[actix_web::main]
async fn main() -> std::io::Result<()> {
    match init_otel() {
        Ok(_) => {
            info!("Successfully configured OTel");
        }
        Err(err) => {
            warn!(
                "Couldn't start OTel! Starting without telemetry: {0}",
                err
            );
        }
    };

    HttpServer::new(|| App::new().wrap(RequestTracing::new()).service(process_temp))
        .bind(("0.0.0.0", 9000))?
        .run()
        .await
}
