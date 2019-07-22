#
import sys
import os
import json
import boto3

messagedir = sys.argv[1]
#s3dir = sys.argv[2]
#print(messagedir)

for filename in os.listdir(messagedir):
    #print(filename)
    json_file = open(os.path.join(messagedir, filename), "r")
    json_data = json.load(json_file)
    body = json_data['body']
    s3url = body.split(",")[0]
    bucketname = s3url.split('/')[3] # + "/cache"
    objectname = s3url.split('/')[5]
    key = "cache/" + objectname
    #print(bucketname + ":" + objectname)
    s3 = boto3.client('s3')
    s3.download_file(bucketname,key,messagedir + "S3/" + objectname)
