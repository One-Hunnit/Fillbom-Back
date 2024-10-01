package kr.co.onehunnit.onhunnit.config.exception;

import java.time.LocalDateTime;

public interface Response<T> {

	static <T> Response<T> of(int code, T data) {
		return new TypeResponse<>(code, data);
	}

	static <T> Response<T> of(T data) {
		return new TypeResponse<>(200, data);
	}

	int getStatus();

	T getData();

	LocalDateTime getTimestamp();

}
