package kr.co.onehunnit.onhunnit.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ImageService {
	private final AmazonS3 s3;

	@Value("${spring.s3.bucket}")
	private String bucketName;

	public String uploadImage(MultipartFile imageFile) {
		String fileName = generateFileName(imageFile.getOriginalFilename());
		uploadImageToNCP(imageFile, fileName);
		return generateImageUrl(fileName);
	}

	private String generateFileName(String baseName) {
		String uniqueId = UUID.randomUUID().toString();
		return uniqueId + "_" + baseName + ".image";
	}

	private void uploadImageToNCP(MultipartFile imageFile, String fileName) {
		try (InputStream inputStream = imageFile.getInputStream()) {
			ObjectMetadata objectMetadata = new ObjectMetadata();
			objectMetadata.setContentLength(imageFile.getSize());
			objectMetadata.setContentType(imageFile.getContentType());

			s3.putObject(new PutObjectRequest(bucketName, fileName, inputStream, objectMetadata)
				.withCannedAcl(CannedAccessControlList.PublicRead));
		} catch (IOException e) {
			throw new RuntimeException("이미지 저장에 실패했습니다: " + e.getMessage());
		}
	}

	private String generateImageUrl(String fileName) {
		return "https://kr.object.ncloudstorage.com/" + bucketName + "/" + fileName;
	}

}
