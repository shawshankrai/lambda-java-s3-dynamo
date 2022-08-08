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