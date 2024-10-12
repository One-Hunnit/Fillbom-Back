package kr.co.onehunnit.onhunnit.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.co.onehunnit.onhunnit.domain.district.District;

@Repository
public interface DistrictRepository extends JpaRepository<District, Long> {

	Optional<District> findByAdmCd(String admCd);

	List<District> findAllByAdmNmContainingOrderById(String admNm);
}
