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
