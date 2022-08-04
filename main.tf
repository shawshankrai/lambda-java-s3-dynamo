terraform {
  backend "s3" {
    bucket = "s3-event-terraform-state-bucket-shashank-iam"
    key = "lambda-java-s3-dynamo"
    region = "us-east-1"
  }
}

locals{
  stack = "aws-reporting-stack"
  name = "aws-reporting-stack"
}

# terraform modules
module "lambda-java-s3-dynamo" {
  source = "./terraform"
}
