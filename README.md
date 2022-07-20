# lambda-java-s3-dynamo

sam package --template-file template.yml --profile shashank-iam  --s3-bucket aws-lambda-code-location-shashank-iam --region us-east-1  --output-template
-file package.yml

sam deploy --template-file D:\Shashank\resources\Amazon\Lambda\Code\lambda-events\package.yml --stack-name aws-report-gen-1 --capabilities CAPABILITY_NA
MED_IAM