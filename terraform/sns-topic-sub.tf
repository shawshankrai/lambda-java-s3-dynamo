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
