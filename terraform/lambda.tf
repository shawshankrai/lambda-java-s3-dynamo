resource "aws_lambda_function" "java_lambda_function" {
  runtime          = "${var.lambda_runtime}"
  filename         = "${var.lambda_source_code}"
  function_name    = "${var.lambda_function_name}"
  handler          = "${var.lambda_function_handler}"
  source_code_hash = "${base64sha256(filebase64(var.lambda_source_code))}"

  timeout          = 300
  memory_size      = 1024
  role             = "${aws_iam_role.iam_for_lambda.arn}"
  depends_on       = [aws_cloudwatch_log_group.log_group]
}
