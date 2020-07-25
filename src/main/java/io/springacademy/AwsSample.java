package io.springacademy;

import java.net.URI;
import java.util.Collections;
import java.util.UUID;

import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.http.apache.ProxyConfiguration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketConfiguration;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketRequest;

public class AwsSample {
	public static void main(String[] args) {
		Region region = Region.US_EAST_2;

		// setting up proxy: https://github.com/aws/aws-sdk-java-v2/issues/751#issuecomment-634237068
		SdkHttpClient httpClient = ApacheHttpClient.builder()
				.proxyConfiguration(
						ProxyConfiguration
								.builder()
								.endpoint(URI.create("http://localhost:9999"))
								.nonProxyHosts(Collections.emptySet())
								.useSystemPropertyValues(false)
								.build()
				).build();

		S3Client s3Client = S3Client.builder()
				.region(region)
				.httpClient(httpClient)
//				uncomment if AWS credentials are not configured locally
//				.credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("...", "...")))
				.build();

		s3Client.listBuckets().buckets()
				.forEach(System.out::println);

		String bucketName = "new-bucket-" + UUID.randomUUID().toString();

		s3Client.createBucket(
				CreateBucketRequest.builder()
						.bucket(bucketName)
						.createBucketConfiguration(
								CreateBucketConfiguration.builder()
										.locationConstraint(region.id())
										.build()
						)
						.build()
		);

		s3Client.listBuckets().buckets()
				.forEach(System.out::println);

		s3Client.deleteBucket(DeleteBucketRequest.builder().bucket(bucketName).build());
	}
}
