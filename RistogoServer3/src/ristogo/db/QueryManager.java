package ristogo.db;

import org.neo4j.driver.Query;
import org.neo4j.driver.Result;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.Values;

import ristogo.common.entities.City;
import ristogo.common.entities.Cuisine;
import ristogo.common.entities.Restaurant;
import ristogo.common.entities.User;

public class QueryManager {
	
	public static Query addUser()
	{
		return new Query("CREATE (user:User {username: $username, password: $password}) ");
	}
	
	public static Query addCity() {
		return new Query("CREATE (:City {name: $name, latitude: $latitude, longitude: $longitude})");
	}
	
	public static Query addCuisine() {
		return new Query("CREATE (:Cuisine {name: $name})");
	}
	
	
	public static Query addRestaurant()
	{		
		return new Query("CREATE (:Restaurant{name: $name, description: $description, price: $price})");	
	}

	
	public static Query likeCuisine() {
		return new Query("MATCH (user:User {username: $username} )"
				+ "MATCH(cuisine:Cuisine {name: $name})"
				+ "CREATE (user)-[:LIKE]->(cuisine)");
	}
	
	public static Query likeRestaurant() {
		return new Query("MATCH (user:User {username: $username} )"
				+ "MATCH(restaurant:Restaurant {name: $name})"
				+ "CREATE (user)-[:LIKE]->(restaurant)");
	}
	
	public static Query serve() {
		return new Query("MATCH (cuisine:Cuisine {name: $cuisine_name} )"
				+ "MATCH (restaurant:Restaurant {name: $restaurant_name})"
				+ "CREATE (restaurant)-[:SERVE]->(cuisine)");
		}
	
	public static Query own() {
		return new Query("MATCH (user:User {username: $username} )"
				+ "MATCH(restaurant:Restaurant {name: $restaurant_name})"
				+ "CREATE (user)-[:OWN]->(restaurant)");
	}
	
	public static Query live() {
		return new Query("MATCH (user:User {username: $username} )"
				+ "MATCH(city:City {name: $city_name})"
				+ "CREATE (user)-[:LIVE]->(city)");
	}
	
	public static Query locate(Transaction tx, City city, Restaurant restaurant) {
		return new Query("MATCH (restaurant:Restaurant {name: $name} )"
				+ "MATCH(city:City {name: $city_name})"
				+ "CREATE (restaurant)-[:LOCATED]->(city)");
	}
	public static Query follow(Transaction tx, User follower, User followee) {
		return new Query("MATCH (follower:User {username: $follower})"
				+ "MATCH (followee:User {username: $followee})"
				+ "CREATE (follower)-[:FOLLOW]->(followee)", 
				Values.parameters("follower", follower.getUsername(),"followee", followee.getUsername()));
	
	}
	
	public static Query deleteUser(Transaction tx, User user) {
		return new Query("MATCH (user:User {username: $username})"
				+ "DETACH DELETE user", Values.parameters("username", user.getUsername()));
	}
	
	public static Query deleteOwned(Transaction tx, User user) {
		return new Query("MATCH (user:User {username: $username})"
				+ "MATCH (user)-[:OWN]->(restaurant:Restaurant)"
				+ "DETACH DELETE restaurant", 
				Values.parameters("username", user.getUsername()));
	}
	
	public static Query deleteRestaurant(Transaction tx, Restaurant restaurant) {
		return new Query("MATCH (restaurant:Restaurant {name: $name})"
				+ "DETACH DELETE restaurant");
	}
	
	public static Result deleteCuisine(Transaction tx, Cuisine cuisine) {
		return tx.run("MATCH (cuisine:Cuisine {name: $name})"
				+ "DETACH DELETE user", Values.parameters("name", cuisine.getName()));
	}
	
	public static Result deleteCity(Transaction tx, City city) {
		return tx.run("MATCH (city:City {name: $name})"
				+ "DETACH DELETE city", Values.parameters("name", city.getName()));
	}
	
	public static Result dislikeCuisine(Transaction tx, User user, Cuisine cuisine) {
		return tx.run("MATCH (user:User {username: $username})"
				+ "MATCH (cuisine:Cuisine {name: $name})"
				+ "MATCH (user)-[r:LIKE]->(cuisine)"
				+ "DELETE r", Values.parameters("username", user.getUsername(), "name",cuisine.getName()));
	}
	
	public static Result dislikeRestaurant(Transaction tx, User user, Restaurant restaurant) {
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
	//Find friends, find favourite restaurants, list users, list restaurants, list cuisine, list cities
	public static Result getRestaurants(Transaction tx) {
		return tx.run("MATCH (restaurant: Restaurant) "
				+ "RETURN collect(restaurant) AS restaurantList");
	}
	
	public static Result getFavouriteRestaurants( Transaction tx, User user) {
		return tx.run("MATCH (user:User {username: $username}) -[:LIKE]-> (restaurant:Restaurant)"
				+ "RETURN restaurant",Values.parameters("username", user.getUsername()));
	}
	public static Result getUsers(Transaction tx) {
		return tx.run("MATCH (user: User)"
				+ "RETURN collect(user) AS userList");
	}
	public static Result getFriends(Transaction tx, User user) {
		return tx.run("MATCH (user:User {username: $username}) -[:FOLLOW]-> (followee:user)"
				+ "RETURN followee.name",Values.parameters("username", user.getUsername()));
	}
	public static Result getCuisine(Transaction tx) {
		return tx.run("MATCH (cuisine: Cuisine)"
				+ "RETURN cuisine.name");
	}
	
	public static Query getUser() {
		return new Query("MATCH (user:User{username:$username}) "
				+ "OPTIONAL MATCH (user)-[:OWN]-(restaurant:Restaurant) "
				+ "RETURN user.username AS username, "
				+ "user.password AS password, "
				+ "restaurant.name AS owned; ");
	};
	
	//TODO SUGGESTIONS
	//1.GET USERS WHICH LIKE A GIVEN CUISINE
	//2.GET USERS WHICH LIVE IN A GIVEN CITY 
	//3.GET CITIES WHICH ARE DISTANT UP TO k KILOMETERS FROM THE GIVEN CITY
	//3.GET USERS WHICH LIVE IN A CITY WHICH IS DISTANT AT MOST k KILOMETERS FROM THE GIVEN CITY 
	//4.FILTER USERS : ONLY ONES THAT NOT HAVE FOLLOW RELATIONSHIP WITH USER
	
	/*
	 * 
	 * 
	 * 
	 * MATCH 
	 * 
	 */
	
	public static Result recommendFriends(Transaction tx, User me, City myCity, Cuisine cuisine, double distance) {
		return tx.run("MATCH (user:User)-[:LIKE]->(cuisine:Cuisine{name:$cuisine})," + 
				"WHERE (EXISTS((user)-[:LIVE]->(my_city:City{name: $mycity})))" + 
				"OR (EXISTS((user)-[:LIVE]->(other_city:City)) AND (distance(other_city, my_city) < $distance ))" +
				"AND NOT EXISTS((me:User{username : $my_name})-[:FOLLOW]->(user))" +
				"RETURN collect(user);", Values.parameters(
						"cuisine", cuisine.getName(), 
						"mycity", myCity.getName(),
						"distance", distance,
						"my_name", me.getUsername()));
		}
	
//	public static Result distance(Transaction tx, City city, City your_city) {
//		return tx.run("MATCH (user)-[:LIVE]->(city:City), (your_city:City {name: $your_city}) WHERE distance(city,your_city)");
//	}
//	public static Result recommendRestaurant(boolean friendOfFriend, Cuisine cuisine, City city, double distance, Price price ) {
//		
//	}
//	
//	//TODO RANKING
//	
//	public static Result rankCity(Restaurant restaurant) {
//		
//	}
//	public static Result rankCuisine(Restaurant restaurant) {
//		
//	}
//	public static Result rank(Restaurant restaurant) {
//		
//	}
//	
}
