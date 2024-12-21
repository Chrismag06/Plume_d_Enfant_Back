package com.poec.plumedenfant.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class S3Config {
	// Cette classe est la config pour le stockage d'images
	
	@Value("${aws.access-key-id}")
	private String awsAccessKeyId;
	
	@Value("${aws.secret-access-key}")
	private String awsSecretAccessKey;
	
	@Value("${aws.region}")
	private String awsRegion;
	
	@Value("${aws.s3.bucket-name}")
	private String bucketName;
	

    @Bean
    AmazonS3 amazonS3() {
		BasicAWSCredentials awsCredentials = new BasicAWSCredentials(awsAccessKeyId, awsSecretAccessKey);
		
		return AmazonS3ClientBuilder.standard()
				.withRegion(Regions.valueOf(awsRegion))
				.withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
				.build();
	}

    @Bean
    String bucketName() {
		return bucketName;
	}
}
