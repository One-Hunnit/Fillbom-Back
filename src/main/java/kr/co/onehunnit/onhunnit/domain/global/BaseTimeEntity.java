package kr.co.onehunnit.onhunnit.domain.global;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.Getter;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTimeEntity {

	@CreatedDate
	@Column(updatable = false)
	private LocalDateTime createdAt;

	@LastModifiedDate
	private LocalDateTime updatedAt;

	@PrePersist
	public void prePersist() {
		LocalDateTime nowInKorea = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
		if (this.createdAt == null) {
			this.createdAt = nowInKorea;
		}
		if (this.updatedAt == null) {
			this.updatedAt = nowInKorea;
		}
	}

}
