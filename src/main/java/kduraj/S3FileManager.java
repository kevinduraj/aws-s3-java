package kduraj;
/*
    The ProfileCredentialsProvider will return your [default]
    credential profile by reading from the credentials file located at
    vi ~/.aws/credentials
    
    [default]
    aws_access_key_id=
    aws_secret_access_key=
 */
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class S3FileManager {

    private final String SUFFIX = "/";
    private AmazonS3 s3Client;
    
    private void authenticate() throws AmazonClientException {
        
        AWSCredentials credentials = null;
        try {
            credentials = new ProfileCredentialsProvider().getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException("Check credentials location (~/.aws/credentials)", e);
        }
        s3Client = new AmazonS3Client(credentials);
        //s3.setRegion(Region.getRegion(Regions.US_WEST_2));
    }

    public void uploadFile(String bucketName, String sourceFile) throws AmazonClientException {
        
        authenticate();
        
        try {
            s3Client.createBucket(bucketName);
            
            // create folder based on current date into bucket
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            
            String folderName = dateFormat.format(date);
            System.out.println("Creating: " + bucketName + "/" + folderName);             
            createFolder(bucketName, folderName, s3Client);

            System.out.println("Uploading a new object to S3 from a file\n");
            String fileName = folderName + SUFFIX + "UC5QHW3FOSmvkj4l39x5LeIw.json";

            s3Client.putObject(new PutObjectRequest(bucketName, fileName, new File(sourceFile)));
            
            
        } catch (AmazonServiceException ase) {
            
            System.out.println("Caught an AmazonServiceException, which means your request made it"
                             + "to Amazon S3, but was rejected with an error response for some reason.");
            
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());

        } catch (AmazonClientException ace) {

            System.out.println("Caught an AmazonClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with S3, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());

        }
    }

    public void listFiles() throws SdkClientException {
        
        System.out.println("Listing buckets");
        for (Bucket bucket : s3Client.listBuckets()) {
            System.out.println(" - " + bucket.getName());
        }
        System.out.println();
    }

    private void createFolder(String bucketName, String folderName, AmazonS3 client) {

        // create meta-data for your folder and set content-length to 0
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(0);

        // create empty content
        InputStream emptyContent = new ByteArrayInputStream(new byte[0]);

        // create a PutObjectRequest passing the folder name suffixed by /
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, folderName + SUFFIX, emptyContent, metadata);

        // send request to S3 to create folder
        client.putObject(putObjectRequest);
    }

}
