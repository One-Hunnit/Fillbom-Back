package kr.co.onehunnit.onhunnit.domain.district.dbmapping;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Properties {
	private String adm_nm;
	private String adm_cd2;
}
