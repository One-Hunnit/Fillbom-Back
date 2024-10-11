package kr.co.onehunnit.onhunnit.domain.district.dbmapping;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class FeatureCollection {
	private String type;
	private List<Feature> features;
}
