package kr.co.onehunnit.onhunnit.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import kr.co.onehunnit.onhunnit.config.response.ResponseDto;
import kr.co.onehunnit.onhunnit.config.response.ResponseUtil;
import kr.co.onehunnit.onhunnit.service.ImageService;
import lombok.RequiredArgsConstructor;

@Tag(name = "이미지")
@RequiredArgsConstructor
@RestController
@RequestMapping("/image")
public class ImageController {

	private final ImageService imageService;

	@PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseDto<String> uploadImage(@RequestParam(value = "image")MultipartFile imageFile) {
		return ResponseUtil.SUCCESS("이미지 업로드에 성공하였습니다.", imageService.uploadImage(imageFile));
	}

}
