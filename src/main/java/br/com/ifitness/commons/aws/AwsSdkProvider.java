package br.com.ifitness.commons.aws;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsSdkProvider {
    @Value("${aws.access.key.id}")
    private String accessKeyId;

    @Value("${aws.secret.access.key}")
    private String secretAccessKey;

    @Bean
    public AmazonS3 getS3Client() {
        BasicAWSCredentials basicAwsCredentials
                = new BasicAWSCredentials(accessKeyId, secretAccessKey);

        return AmazonS3ClientBuilder
            .standard()
            .withRegion(Regions.US_EAST_1)
            .withCredentials(
                new AWSStaticCredentialsProvider(
                        basicAwsCredentials
                )
            ).build();
    }

}
