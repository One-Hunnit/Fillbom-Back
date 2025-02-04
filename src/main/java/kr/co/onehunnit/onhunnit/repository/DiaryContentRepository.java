package kr.co.onehunnit.onhunnit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.co.onehunnit.onhunnit.domain.diarycontent.DiaryContent;

@Repository
public interface DiaryContentRepository extends JpaRepository<DiaryContent, Long> {
}
