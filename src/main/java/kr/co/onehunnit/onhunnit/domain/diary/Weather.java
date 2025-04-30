package kr.co.onehunnit.onhunnit.domain.diary;

public enum Weather {

	SUNNY("맑음"),
	CLOUDY("구름"),
	RAINY("비"),
	SNOWY("눈"),
	FOG("안개");

	private final String description;

	private Weather(String description) {
		this.description = description;
	}

}
