package ristogo.db;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.TransactionWork;
import org.neo4j.driver.Values;

import ristogo.common.entities.City;
import ristogo.common.entities.Cuisine;
import ristogo.common.entities.Restaurant;
import ristogo.common.entities.User;

public class DBManager implements AutoCloseable {
	private Driver driver;
	private static String uri = "neo4j://localhost:7687";
	private static String user = "ristogo";
	private static String password = "ristogo";
	private static DBManager instance = null; 
	private DBManager(String uri, String user,String password) {
		driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
	}
	
	public static DBManager getInstance(String uri, String user, String password) {
		if(instance == null)
			instance = new DBManager(uri, user, password);
		return instance;
	}
	
	public static DBManager getInstance() {
		return getInstance(uri, user, password);
	}
	
	public Session getSession() {
		return driver.session();
	}
	
	@Override
	public void close() throws Exception
	{
		driver.close();	
	}

	public static String getUri()
	{
		return uri;
	}

	public static void setUri(String uri)
	{
		DBManager.uri = uri;
	}

	public static String getUser()
	{
		return user;
	}

	public static void setUser(String user)
	{
		DBManager.user = user;
	}

	public static String getPassword()
	{
		return password;
	}

	public static void setPassword(String password)
	{
		DBManager.password = password;
	}
	
	//QUERY
	
	//ADD USER
	public static Result addUser(Transaction tx, User user)
	{
		return tx.run("CREATE (:User {username: $username, password: $password})", 
				Values.parameters("username", user.getUsername() , 
						"password", user.getPasswordHash()));
	}
	
	public static Result addCity(Transaction tx, City city) {
		return tx.run("MERGE (:City {name: $name, latitude: $latitude, longitude: $longitude})",
				Values.parameters("name", city.getName(), 
						"latitude",city.getLatitude(), 
						"longitude", city.getLongitude()));
	}
	
	public static Result addCuisine(Transaction tx, Cuisine cuisine) {
		return tx.run("MERGE (:Cuisine {name: $name})", Values.parameters("name", cuisine.getName()));
	}
	
	
	public static Result addRestaurant(Transaction tx, Restaurant restaurant)
	{
			
		return tx.run("CREATE (:Restaurant{name: $name, description: $description, price: $price})", 
				Values.parameters("name", restaurant.getName(),
						"description", restaurant.getDescription(),
						"price", restaurant.getPrice().name()));
	}

	
	public static Result like(Transaction tx, User user, Cuisine cuisine) {
		return tx.run("MATCH (user:User {username: $username} )"
				+ "MATCH(cuisine:Cuisine {name: $name})"
				+ "MERGE (user)-[:LIKE]->(cuisine)", 
				Values.parameters("username", user.getUsername(),"name",cuisine.getName()));
	}
	
	public static Result like(Transaction tx, User user, Restaurant restaurant) {
		return tx.run("MATCH (user:User {username: $username} )"
				+ "MATCH(restaurant:Restaurant {name: $name})"
				+ "MERGE (user)-[:LIKE]->(restaurant)", 
				Values.parameters("username", user.getUsername(),"name",restaurant.getName()));
	}
	
	public static Result serve(Transaction tx, Restaurant restaurant, Cuisine cuisine) {
		return tx.run("MATCH (cuisine:Cuisine {name: $cuisine_name} )"
				+ "MATCH(restaurant:Restaurant {name: $restaurant_name})"
				+ "MERGE (restaurant)-[:SERVE]->(cuisine)", 
				Values.parameters("cuisine_name", restaurant.getName(),"restaurant_name",restaurant.getName()));
	}
	
	public static Result own(Transaction tx, User user, Restaurant restaurant) {
		return tx.run("MATCH (user:User {username: $username} )"
				+ "MATCH(restaurant:Restaurant {name: $restaurant_name})"
				+ "MERGE (user)-[:OWN]->(restaurant)", 
				Values.parameters("username", user.getUsername(),"restaurant_name",restaurant.getName()));
	}
	
	public static Result locate(Transaction tx, City city, User user) {
		return tx.run("MATCH (user:User {username: $username} )"
				+ "MATCH(city:City {name: $city_name})"
				+ "MERGE (user)-[:LIVE]->(city)", 
				Values.parameters("city_name", city.getName(),"username",user.getUsername()));
	}
	
	public static Result locate(Transaction tx, City city, Restaurant restaurant) {
		return tx.run("MATCH (restaurant:Restaurant {name: $name} )"
				+ "MATCH(city:City {name: $city_name})"
				+ "MERGE (restaurant)-[:LOCATED]->(city)", 
				Values.parameters("city_name", city.getName(),"name", restaurant.getName()));
	}
	public static Result follow(Transaction tx, User follower, User followee) {
		return tx.run("MATCH (follower:User {username: $follower})"
				+ "MATCH (followee:User {username: $followee})"
				+ "MERGE (follower)-[:FOLLOW]->(followee)", 
				Values.parameters("follower", follower.getUsername(),"followee", followee.getUsername()));
	
	}
	
	public static Result delete(Transaction tx, User user) {
		return tx.run("MATCH (user:User {username: $username})"
				+ "DETACH DELETE user", Values.parameters("username", user.getUsername()));
	}
	
	public static Result deleteOwned(Transaction tx, User user) {
		return tx.run("MATCH (user:User {username: $username})"
				+ "MATCH (user)-[:OWN]->(restaurant:Restaurant)"
				+ "DETACH DELETE restaurant", 
				Values.parameters("username", user.getUsername()));
	}
	
	public static Result delete(Transaction tx, Restaurant restaurant) {
		return tx.run("MATCH (restaurant:Restaurant {name: $name})"
				+ "DETACH DELETE restaurant", 
				Values.parameters("name", restaurant.getName()));
	}
	
	public static Result delete(Transaction tx, Cuisine cuisine) {
		return tx.run("MATCH (cuisine:Cuisine {name: $name})"
				+ "DETACH DELETE user", Values.parameters("name", cuisine.getName()));
	}
	
	public static Result delete(Transaction tx, City city) {
		return tx.run("MATCH (city:City {name: $name})"
				+ "DETACH DELETE city", Values.parameters("name", city.getName()));
	}
	
	public static Result dislike(Transaction tx, User user, Cuisine cuisine) {
		return tx.run("MATCH (user:User {username: $username})"
				+ "MATCH (cuisine:Cuisine {name: $name})"
				+ "MATCH (user)-[r:LIKE]->(cuisine)"
				+ "DELETE r", Values.parameters("username", user.getUsername(), "name",cuisine.getName()));
	}
	
	public static Result dislike(Transaction tx, User user, Restaurant restaurant) {
		return tx.run("MATCH (user:User {username: $username})"
				+ "MATCH (restaurant:Restaurant {name: $name})"
				+ "MATCH (user)-[r:LIKE]->(restaurant)"
				+ "DELETE r", Values.parameters("username", user.getUsername(), "name",restaurant.getName()));
	}
	
	public static Result unfollow(Transaction tx, User follower, User followee) {
		return tx.run("MATCH (follower:User {username: $follower})"
				+ "MATCH (followee:User {username: $followee})"
				+ "MATCH (follower)-[r:FOLLOW]->(followee)"
				+ "DELETE r", 
				Values.parameters("follower", follower.getUsername(),
				"followee", followee.getUsername()));
	}
	
	public static Result unserve(Transaction tx, Restaurant restaurant, Cuisine cuisine) {
		return tx.run("MATCH (restaurant:Restaurant {name: $res_name})"
				+ "MATCH (cuisine:Cuisine {name: $cuis_name})"
				+ "MATCH (restaurant)-[r:SERVE]->(cuisine)"
				+ "DELETE r", Values.parameters("res_name", restaurant.getName(), "cuis_name", cuisine.getName()));
	}
	
	//TODO LISTS
	//TODO SUGGESTIONS
	
	
}
