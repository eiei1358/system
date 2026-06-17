#!/bin/bash

echo "Starting Spring Boot Application..."
echo ""

# Check if gradlew exists
if [ ! -f "$(dirname "$0")/gradlew" ]; then
    echo "Error: gradlew not found!"
    exit 1
fi

# Run with dev profile
"$(dirname "$0")/gradlew" bootRun --args="--spring.profiles.active=dev"
