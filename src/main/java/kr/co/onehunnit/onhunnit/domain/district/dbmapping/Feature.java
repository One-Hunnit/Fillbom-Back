package kr.co.onehunnit.onhunnit.domain.district.dbmapping;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Feature {
	private String type;
	private Properties properties;
	private Geometry geometry;
}
