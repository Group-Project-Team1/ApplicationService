package com.beaconfire.applicationservice.config;

import com.google.gson.Gson;
import software.amazon.awssdk.regions.Region;
import com.beaconfire.applicationservice.domain.AwsSecrets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;


@Configuration
public class S3Config {


    private String accessKeyId;

    private String accessKeySecret;
    @Value("${aws.s3.region}")
    private String region;

    @Bean
    public AmazonS3 s3Client() {
        AwsSecrets awsSecrets = getSecret();
        this.accessKeyId = awsSecrets.getUsername();
        this.accessKeySecret = awsSecrets.getPassword();

        AWSCredentials credentials = new BasicAWSCredentials(this.accessKeyId, this.accessKeySecret);
        return AmazonS3ClientBuilder.standard()
                .withRegion(Regions.fromName(region))
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();

    }

    public AwsSecrets getSecret() {

        String secretName = "AwsCredentials";
        Region region = Region.of("us-east-2");

        // Create a Secrets Manager client
        SecretsManagerClient client = SecretsManagerClient.builder()
                .region(region)
                .build();

        GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

        GetSecretValueResponse getSecretValueResponse;

        try {
            getSecretValueResponse = client.getSecretValue(getSecretValueRequest);
        } catch (Exception e) {
            // For a list of exceptions thrown, see
            // https://docs.aws.amazon.com/secretsmanager/latest/apireference/API_GetSecretValue.html
            throw e;
        }

        String secret = getSecretValueResponse.secretString();

        Gson gson = new Gson();
        return gson.fromJson(secret, AwsSecrets.class);
        // Your code goes here.
    }



}
