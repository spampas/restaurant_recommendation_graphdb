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
	private static String user = "neo4j";
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
	
}
