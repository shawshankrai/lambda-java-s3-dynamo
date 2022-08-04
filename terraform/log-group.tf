resource "aws_cloudwatch_log_group" "log_group" {
  name = "/aws/lambda/lambda-java-s3-dynamo"
}