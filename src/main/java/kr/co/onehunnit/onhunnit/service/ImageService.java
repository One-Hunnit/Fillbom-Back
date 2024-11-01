package kr.co.onehunnit.onhunnit.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import kr.co.onehunnit.onhunnit.dto.image.Base64ImageDto;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ImageService {
	private final AmazonS3 s3;

	@Value("${spring.s3.bucket}")
	private String bucketName;

	public String uploadImage(Base64ImageDto base64ImageDto) {
		byte[] imageBytes = Base64.getDecoder().decode(base64ImageDto.getBase64Image());
		String fileName = generateFileName();
		uploadImageToNCP(imageBytes, fileName);
		return generateImageUrl(fileName);
	}

	private String generateFileName() {
		String uniqueId = UUID.randomUUID().toString();
		String datePart = new SimpleDateFormat("yyyyMMdd").format(new Date());
		return datePart + "_" + uniqueId + ".image";
	}

	private void uploadImageToNCP(byte[] imageBytes, String fileName) {
		try (InputStream inputStream = new ByteArrayInputStream(imageBytes)) {
			ObjectMetadata objectMetadata = new ObjectMetadata();
			objectMetadata.setContentLength(imageBytes.length);
			objectMetadata.setContentType("image/jpeg");

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
