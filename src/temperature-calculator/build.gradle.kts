plugins {
	java
	id("org.springframework.boot") version "3.4.2"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "io.temp.calculator"
version = "0.0.1"

java {
	sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
	mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web");
    implementation("net.logstash.logback:logstash-logback-encoder:7.4");
    implementation("io.opentelemetry:opentelemetry-api:1.47.0");
}

tasks.named("jar") {
	enabled = false
}

tasks {
	bootJar {
		archiveFileName.set("tempcalculator-0.0.1.jar")
	}
}
