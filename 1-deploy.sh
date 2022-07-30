#!/bin/bash
set -eo pipefail
TEMPLATE=template.yml
ARTIFACT_BUCKET=aws-lambda-code-location-shashank-iam
PROFILE=shashank-iam
REGION=us-east-1
STACK=aws-reporting-stack-1
OUTPUTFILE=packaged.yaml

mvn package

sam package --template-file $TEMPLATE --s3-bucket $ARTIFACT_BUCKET --output-template-file $OUTPUTFILE --profile $PROFILE --region $REGION
sam deploy --template-file $OUTPUTFILE --stack-name $STACK --capabilities CAPABILITY_NAMED_IAM
