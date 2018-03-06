package kduraj;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class App {
    
    public static void main(String[] args) throws IOException {
        
        // String bucketName = UUID.randomUUID();
       
        S3FileManager s3 = new S3FileManager();
        s3.uploadFile("youtube-json", "/Users/kevinduraj/UC5QHW3FOSmvkj4l39x5LeIw.json");
        s3.listFiles();
        
    }
}
