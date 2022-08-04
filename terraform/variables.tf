variable "aws_access_key" {
  # set aws access key
  default = ""
}

variable "aws_secret_key" {
  # set aws secret key
  default = ""
}

variable "region" {
  # set aws region
  default = "us-east-1"
}

variable "s3_data_bucket" {
  # set bucket name
  default = "s3-event-data-bucket-shashank-iam"
}

variable "lambda_function_handler" {
  default = "handler.Handler"
}

variable "lambda_runtime" {
  default = "java11"
}

variable "lambda_source_code" {
  default = "target/lambda-java-s3-dynamo-1.0-SNAPSHOT.jar"
}

variable "lambda_function_name" {
  default = "lambda-java-s3-dynamo"
}

variable "sns_email_endpoint" {
  default = "shashank.rai92@gmail.com"
}

variable "sns_topic_display_name" {
  default = "Report Status"
}