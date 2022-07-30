#!/bin/bash
set -eo pipefail
gradle clean
gradle build
gradle -q packageLibs
mv build/distributions/lambda-java-s3-dynamo-1.0-SNAPSHOT.zip build/lambda-java-s3-dynamo-1.0-SNAPSHOT.zip