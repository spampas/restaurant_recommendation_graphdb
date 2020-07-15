package ristogo.common.net.entities;

public class StatisticInfo extends Entity
{
	private static final long serialVersionUID = 3781936224544711696L;

	private int likes;
	private int cuisineRank;
	private int cityRank;
	private int cuisineCityRank;

	public StatisticInfo(int likes, int cuisineRank, int cityRank, int cuisineCityRank)
	{
		this.likes = likes;
		this.cityRank = cityRank;
		this.cuisineRank = cuisineRank;
		this.cuisineCityRank = cuisineCityRank;
	}

	public int getLikes()
	{
		return likes;
	}

	public int getCuisineRank()
	{
		return cuisineRank;
	}

	public int getCityRank()
	{
		return cityRank;
	}

	public int getCuisineCityRank()
	{
		return cuisineCityRank;
	}

	public void setLikes(int likes)
	{
		this.likes = likes;
	}

	public void setCuisineRank(int cuisineRank)
	{
		this.cuisineRank = cuisineRank;
	}

	public void setCityRank(int cityRank)
	{
		this.cityRank = cityRank;
	}

	public void setCuisineCityRank(int cuisineCityRank)
	{
		this.cuisineCityRank = cuisineCityRank;
	}

}
