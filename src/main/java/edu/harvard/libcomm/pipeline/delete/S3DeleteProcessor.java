package edu.harvard.libcomm.pipeline.delete;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import edu.harvard.libcomm.message.LibCommMessage;
import edu.harvard.libcomm.pipeline.IProcessor;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by mjv162 on 11/1/2018.
 */
public class S3DeleteProcessor implements IProcessor {

    @Autowired
    private AmazonS3 s3Client;

    private String bucket;





    public void processMessage(LibCommMessage libCommMessage) throws Exception {
        String recordId = libCommMessage.getPayload().getData();
        System.out.println(bucket + " : " + recordId);
        s3Client.deleteObject(new DeleteObjectRequest(bucket, recordId));
        //deleteFrom3(recordId);
    }


    public void setBucket(String s) {
        this.bucket = s;
    }

    public String getBucket() {
        return this.bucket;
    }


}
