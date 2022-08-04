resource "aws_iam_role_policy" "lambda" {
  name = "iam_for_s3_lambda_dynamo_sns_policy"
  role = aws_iam_role.iam_for_lambda.id

  policy = jsonencode({
    Version   = "2012-10-17"
    Statement = [
      {
        Effect   = "Allow"
        Action   = [
          "s3:DeleteObject",
          "s3:ListBucket",
          "s3:HeadObject",
          "s3:GetObject",
          "s3:GetObjectVersion",
          "s3:PutObject",
          "dynamodb:BatchGetItem",
          "dynamodb:GetItem",
          "dynamodb:Query",
          "dynamodb:Scan",
          "dynamodb:BatchWriteItem",
          "dynamodb:PutItem",
          "dynamodb:UpdateItem",
          "logs:CreateLogGroup",
          "logs:CreateLogStream",
          "logs:PutLogEvents",
          "sns:Publish",
          "lambda:InvokeFunction"
        ]
        Resource = [
          "*",
        ]
      }
    ]
  })
}