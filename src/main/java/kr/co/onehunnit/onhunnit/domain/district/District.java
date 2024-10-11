package kr.co.onehunnit.onhunnit.domain.district;

import java.util.ArrayList;
import java.util.List;

import org.locationtech.jts.geom.MultiPolygon;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import kr.co.onehunnit.onhunnit.domain.safezone.SafeZone;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "districts")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class District {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String admNm;
	private String admCd;

	@Column(columnDefinition = "geometry(MULTIPOLYGON,4326)")
	private MultiPolygon geom;

	@Builder.Default
	@OneToMany(mappedBy = "district")
	private List<SafeZone> safeZoneList = new ArrayList<>();

}

