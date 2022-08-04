resource "aws_s3_bucket" "s3_data_bucket" {
  bucket = "${var.s3_data_bucket}"
}

resource "aws_s3_bucket_notification" "bucket_notification" {
  bucket = aws_s3_bucket.s3_data_bucket.id

  lambda_function {
    lambda_function_arn = aws_lambda_function.java_lambda_function.arn
    events              = ["s3:ObjectCreated:*"]
    filter_prefix       = "inbound/citi"
    filter_suffix       = ".csv"
  }

  depends_on = [aws_lambda_permission.s3_permission_to_trigger_lambda]
}

resource "aws_lambda_permission" "s3_permission_to_trigger_lambda" {
  statement_id  = "AllowExecutionFromS3Bucket"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.java_lambda_function.arn
  principal     = "s3.amazonaws.com"
  source_arn    = aws_s3_bucket.s3_data_bucket.arn
}

resource "aws_iam_role" "iam_for_lambda" {
  name = "iam_for_lambda"

  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": "sts:AssumeRole",
      "Principal": {
        "Service": "lambda.amazonaws.com"
      },
      "Effect": "Allow",
      "Sid": ""
    }
  ]
}
EOF
}

resource "aws_lambda_function" "java_lambda_function" {
  runtime          = "${var.lambda_runtime}"
  filename         = "${var.lambda_source_code}"
  function_name    = "${var.lambda_function_name}"
  handler          = "${var.lambda_function_handler}"
  source_code_hash = "${base64sha256(filebase64(var.lambda_source_code))}"

  timeout          = 60
  memory_size      = 1024
  role             = "${aws_iam_role.iam_for_lambda.arn}"
  depends_on       = [aws_cloudwatch_log_group.log_group]
}

resource "aws_sns_topic" "report_generation_email" {
  name = "report_generation_email"
  display_name = "${var.sns_topic_display_name}"
}

resource "aws_sns_topic_subscription" "subscription" {
  topic_arn = aws_sns_topic.report_generation_email.arn
  protocol = "email"
  endpoint = "${var.sns_email_endpoint}"
}

resource "aws_lambda_function_event_invoke_config" "java_lambda_function_destination_config" {
  function_name = aws_lambda_function.java_lambda_function.function_name

  destination_config {
    on_failure {
      destination = aws_sns_topic.report_generation_email.arn
    }

    on_success {
      destination = aws_sns_topic.report_generation_email.arn
    }
  }
}

resource "aws_dynamodb_table" "Row" {
  name           = "Row"
  billing_mode   = "PROVISIONED"
  read_capacity  = 1
  write_capacity = 1
  hash_key       = "id"

  attribute {
    name = "id"
    type = "S"
  }

  tags = {
    Name        = "dynamodb-table-Row"
    Environment = "UAT"
  }
}

/*
resource "aws_lambda_alias" "test_java_lambda_function_alias" {
  name             = "test_java_lambda_function_alias"
  description      = "a sample description"
  function_name    = aws_lambda_function.java_lambda_function.function_name
  function_version = "1"
}*/
