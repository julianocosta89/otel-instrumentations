#!/bin/bash
# Endpoint without the measurements parameter
BASE_URL="localhost:8080/simulateTemperature?location=London"

while true; do
    # Generate a random count for how many curl requests (between 1 and 10)
    COUNT=$(( ( RANDOM % 10 ) + 1 ))
    echo "[$(date)] Executing $COUNT curl requests..."

    for (( i=1; i<=COUNT; i++ )); do
        # Generate a random measurement value (for example, between 1 and 10)
        MEASUREMENTS=$(( ( RANDOM % 10 ) + 1 ))
        # Perform the curl request in the background with the random measurements value
        curl -s "${BASE_URL}&measurements=${MEASUREMENTS}" > /dev/null &
    done

    # Wait for one minute before the next batch of requests
    sleep 60
done
