#!/bin/sh
kill $(cat pid-aws-camelk-file.pid)
#kill $(cat pid-aws-camelk-s3.pid)