import argparse
import boto.sqs
import javaproperties
import json
import os

#config = configparser.RawConfigParser
##config.read(os.path.join(os.path.abspath(os.path.dirname(__file__)), './src/main/resources','librarycloud.env.properties')
#config.read('./src/main/resources','librarycloud.env.properties')
#print(config.get('','aws.access.key'))

with open('./src/main/resources/librarycloud.env.properties', 'r') as f:
        properties_dict = javaproperties.load(f)
aws_key = properties_dict.get('aws.access.key')
aws_secret = properties_dict.get('aws.secret.key')

parser = argparse.ArgumentParser(description='Saves all messages from an AWS SQS queue into a folder.')

parser.add_argument(
    '-q', '--queue', dest='queue', type=str, required=True,
    help='prod-dead-letter')

parser.add_argument(
    '-a', '--account', dest='account', type=str,
    help='Account')

parser.add_argument(
    '-o', '--output', dest='output', type=str, default='queue-messages',
    help='output')

parser.add_argument(
    '-r', '--region', dest='aws_region', type=str, required=True,
    help='Region')

#parser.add_argument(
#    '-k', '--key', dest='aws_key', type=str, required=True,
#    help='Key')

#parser.add_argument(
#    '-s', '--secret', dest='aws_secret', type=str, required=True,
#    help='Secret')

parser.add_argument(
    '-d', '--delete', dest='delete', default=False, action='store_true',
    help='Whether or not to delete saved messages from the queue.')

parser.add_argument(
    '-v', '--visibility', dest='visibility', type=int, default=60,
    help='The message visibility timeout for saved messages.')

args = parser.parse_args()

if not os.path.exists(args.output):
    os.makedirs(args.output)

conn = boto.sqs.connect_to_region(
    args.aws_region,
    aws_access_key_id=aws_key,
    aws_secret_access_key=aws_secret)

queue = conn.get_queue(args.queue, owner_acct_id=args.account)

count = 0
while True:
    messages = queue.get_messages(
            num_messages=10,
            message_attributes=['All'],
            visibility_timeout=args.visibility)
    if len(messages) == 0: break

    for msg in messages:
        filename = os.path.join(args.output, msg.id)
        obj = { 'id': msg.id,
                'attributes': msg.message_attributes,
                'body': msg.get_body() }

        with open(filename, 'w') as f:
            json.dump(obj, f, indent=2)
            count += 1
            #print "Saved message to {}".format(filename)
            if args.delete:
                queue.delete_message(msg)

#print '{} messages saved'.format(count)
