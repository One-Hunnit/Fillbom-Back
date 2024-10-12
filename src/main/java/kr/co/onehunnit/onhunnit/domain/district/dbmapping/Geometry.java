package kr.co.onehunnit.onhunnit.domain.district.dbmapping;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Geometry {
	private String type;
	private double[][][][] coordinates;
}
