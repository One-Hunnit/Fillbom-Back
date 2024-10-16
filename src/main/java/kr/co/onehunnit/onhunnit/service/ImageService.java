package kr.co.onehunnit.onhunnit.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ImageService {

	private final ResourceLoader resourceLoader;
	private final AmazonS3 s3;

	@Value("${spring.s3.bucket}")
	private String bucketName;

	public String convertUrlToImage(String imageUrl) {
		Resource imageResource = loadImageResource(imageUrl);
		String fileName = generateFileName(imageUrl);
		uploadImageToNCP(imageResource, fileName);
		return generateImageUrl(fileName);
	}

	private Resource loadImageResource(String imageUrl) {
		Resource imageResource = resourceLoader.getResource(imageUrl);
		if (!imageResource.exists()) {
			throw new RuntimeException("이미지를 찾을 수 없습니다: " + imageUrl);
		}
		return imageResource;
	}

	private String generateFileName(String baseName) {
		String uniqueId = UUID.randomUUID().toString();
		return uniqueId + "_" + baseName + ".jpg";
	}

	private void uploadImageToNCP(Resource imageResource, String fileName) {
		try (InputStream inputStream = imageResource.getInputStream()) {
			ObjectMetadata objectMetadata = new ObjectMetadata();
			objectMetadata.setContentLength(imageResource.contentLength());
			objectMetadata.setContentType("image/jpeg");

			s3.putObject(new PutObjectRequest(bucketName, fileName, inputStream, objectMetadata).withCannedAcl(
				CannedAccessControlList.PublicRead));
		} catch (IOException e) {
			throw new RuntimeException("이미지 저장에 실패했습니다.");
		}
	}

	private String generateImageUrl(String fileName) {
		return "https://kr.object.ncloudstorage.com/" + bucketName + "/" + fileName;
	}

}
