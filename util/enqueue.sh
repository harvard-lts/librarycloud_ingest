#!/bin/bash

while getopts ":q:s:e:f:u:" opt; do
    case ${opt} in
        q )
            QUEUE=$OPTARG
            ;;
        s )
            SOURCE=$OPTARG
            ;;
        e )
            ENVIRONMENT=$OPTARG
            ;;
        f )
            FILE_PATH=$OPTARG
            ;;
        u )
            FILE_URL=$OPTARG
            ;;
        \? )
            echo "Invalid option: $OPTARG" 1>&2
            ;;
        : )
            echo "Invalid option: $OPTARG requires an argument" 1>&2
            ;;
    esac
done

if [[ ! $QUEUE || ! $SOURCE || ! $ENVIRONMENT || ( ! $FILE_PATH && ! $FILE_URL ) ]]; then
    echo "Usage: enqueue.sh -q QUEUE_NAME -s SOURCE -e ENVIRONMENT [-f FILE] | [-u S3_URL]"
    exit 1
fi

if [[ $FILE_PATH ]]; then
    FILE_NAME=$(basename $FILE_PATH)
    TARGET_FILE_NAME=`echo $FILE_NAME | sed 's/#//g'`
    TARGET_BUCKET=harvard.librarycloud.upload.$ENVIRONMENT.$SOURCE

    if ! aws s3 cp $FILE_PATH s3://$TARGET_BUCKET/$TARGET_FILE_NAME; then
        echo "Creating bucket $TARGET_BUCKET"
        aws s3 mb s3://$TARGET_BUCKET
        aws s3api put-bucket-lifecycle --bucket $TARGET_BUCKET --lifecycle-configuration '{"Rules":[{"Status":"Enabled","Prefix":"","Expiration":{"Days":30},"ID":"Delete old items"}]}'
    fi

    if ! aws s3 cp $FILE_PATH s3://$TARGET_BUCKET/$TARGET_FILE_NAME; then
        echo "Error uploading file"
        exit 1
    fi

    SIGNED_FILE_URL=`sign_s3_url.bash --bucket $TARGET_BUCKET --file-path $TARGET_FILE_NAME --minute-expire 1440`

    # Need to escape ampersands in the replacement string, or sed will do odd stuff
    FILE_URL=`echo $SIGNED_FILE_URL | sed 's|&|\\\&amp;|g'`
else
    FILE_NAME=`echo $FILE_URL | sed -e  "s/.*\///"`
fi

if [[ $QUEUE = 'ingest' ]]; then
    QUEUE="ingest-$SOURCE"
    FORMAT='UNUSED_FORMAT'
elif [[ $QUEUE = 'normalize-marcxml' ]]; then
    FORMAT='marcxml'
else
    FORMAT='mods'
fi


# Create librarycloud command
(sed -e "s|TARGET|$FILE_URL|" | sed -e "s|SOURCE|$SOURCE|" | sed -e "s|COMMAND|ingest|" | sed -e "s|FORMAT|$FORMAT|") > $FILE_NAME.command.xml <<EOF
<?xml version="1.0" encoding="UTF-8"?>
<lib_comm_message>
    <command>COMMAND</command>
    <payload>
        <source>SOURCE</source>
        <format>FORMAT</format>
        <filepath>TARGET</filepath>
    </payload>
</lib_comm_message>
EOF

# Copy ingest command to target queue
aws sqs create-queue --queue-name=$SQS_ENVIRONMENT-$QUEUE_NAME
aws sqs send-message --queue=https://sqs.us-east-1.amazonaws.com/$ENVIRONMENT-$QUEUE --message-body="$(<$FILE_NAME.command.xml)"

rm $FILE_NAME.command.xml
