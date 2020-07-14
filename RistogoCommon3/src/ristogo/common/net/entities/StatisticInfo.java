package ristogo.common.net.entities;

public class StatisticInfo extends Entity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3781936224544711696L;
	
	private int likes;
	private int cuisineRank;
	private int cityRank;
	private int cuisineCityRank;
	
	public StatisticInfo() {
		super();
		likes = 0;
		cuisineRank = 0;
		cityRank = 0;
		cuisineCityRank = 0;
	}
	
	public StatisticInfo(int likes, int cuisineRank, int cityRank, int cuisineCityRank) {
		this.likes = likes;
		this.cityRank = cityRank;
		this.cuisineRank = cuisineRank;
		this.cuisineCityRank = cuisineCityRank;
	}
	
}
