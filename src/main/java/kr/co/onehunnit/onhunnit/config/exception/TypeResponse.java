package kr.co.onehunnit.onhunnit.config.exception;

import java.time.LocalDateTime;

public class TypeResponse<T> implements Response<T> {

	private int status;
	private T data;
	private LocalDateTime timestamp;

	public TypeResponse(int status, T data) {
		this.status = status;
		this.data = data;
		this.timestamp = LocalDateTime.now();
	}

	@Override
	public int getStatus() {
		return status;
	}

	@Override
	public T getData() {
		return data;
	}

	@Override
	public LocalDateTime getTimestamp() {
		return timestamp = LocalDateTime.now();
	}
}
