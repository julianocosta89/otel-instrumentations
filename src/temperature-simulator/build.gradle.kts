plugins {
	java
	id("org.springframework.boot") version "3.4.2"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "io.temp.simulator"
version = "0.0.1"

java {
	sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
	mavenCentral()
}

dependencyManagement {
  imports {
    mavenBom("io.opentelemetry:opentelemetry-bom:1.47.0")
  }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("io.opentelemetry:opentelemetry-api");
    implementation("io.opentelemetry:opentelemetry-sdk");
    implementation("io.opentelemetry:opentelemetry-exporter-otlp");
    implementation("io.opentelemetry.semconv:opentelemetry-semconv:1.30.0-rc.1");
    implementation("io.opentelemetry.instrumentation:opentelemetry-logback-appender-1.0:2.12.0-alpha");
    implementation("io.opentelemetry:opentelemetry-sdk-extension-autoconfigure");
}

tasks.named("jar") {
	enabled = false
}

tasks {
	bootJar {
		archiveFileName.set("tempsimulator-0.0.1.jar")
	}
}
