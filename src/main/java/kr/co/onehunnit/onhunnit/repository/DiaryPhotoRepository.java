package kr.co.onehunnit.onhunnit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.co.onehunnit.onhunnit.domain.diaryphoto.DiaryPhoto;

@Repository
public interface DiaryPhotoRepository extends JpaRepository<DiaryPhoto, Long> {
}
