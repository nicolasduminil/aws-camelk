#!/bin/sh
aws s3 ls | cut -d" " -f 3 | xargs -I{} aws s3 rb s3://{} --force