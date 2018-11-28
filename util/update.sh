#!/bin/bash

# Script to kick off an ingest
# 
# Usage:    ingest.sh [INSTRUCTION] [SOURCE] [SQS_ENVIRONMENT] [DATA_FILE] 
# Example:  ingest.sh ingest aleph test ab.bib.00.20140808.full.mrc 
# 
# Example (multiple files): find {DIRECTORY} | xargs -P 10 -L 1 ingest.sh oasis test
# 
# TODO: Make more robust

INGEST_INSTRUCTION=$1
DATA_SOURCE_NAME=$2
SQS_ENVIRONMENT=$3
SOURCE_FILE_PATH=$4
SOURCE_FILE_NAME=$(basename $SOURCE_FILE_PATH)

if [ $# -ne 4 ]; then
    echo "Usage: ingest.sh [INSTRUCTION] [SOURCE] [SQS_ENVIRONMENT] [DATA_FILE] "
    exit 1
fi

if [ ! -f $SOURCE_FILE_PATH ]; then
  echo "Data file does not exist"
  exit 1
fi

# Copy ingest command to target queue
aws sqs create-queue --queue-name=$SQS_ENVIRONMENT-$INGEST_INSTRUCTION-$DATA_SOURCE_NAME
aws sqs send-message --queue=http://sqs.us-east-1.amazonaws.com/$SQS_ENVIRONMENT-$INGEST_INSTRUCTION-$DATA_SOURCE_NAME --message-body="$(<$SOURCE_FILE_PATH)"
