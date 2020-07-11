package ristogo.db;

import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;

import ristogo.common.entities.Customer;
import ristogo.common.entities.Owner;
import ristogo.common.entities.Restaurant;
import ristogo.common.entities.User;

public class QueryExecutor {
	public static User getUserByUsername(String username) {
		try(Session session = DBManager.getInstance().getSession()){
			User user = session.readTransaction(tx -> {
				Result result = tx.run(QueryManager.getUser().withParameters(Values.parameters("username", username)));
				if(result.hasNext()) {
					Record record= result.next();
					String uname = record.get("username").asString();
					String password = record.get("password").asString();
					boolean owner = record.get("owned").asString() != null;
					if(owner)
						return new Owner(uname, password);
					return new Customer(uname,password);
				}
				return null;
			});
			return user;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static boolean registerUser(User user, Restaurant restaurant) {
		try(Session session = DBManager.getInstance().getSession()){
			return session.readTransaction(tx -> {
				Result result = tx.run(QueryManager
						.addUser()
						.withParameters(Values.parameters("username", user.getUsername())));
				result.consume();
				if(restaurant != null)
				result = tx.run(QueryManager
						.addRestaurant()
						.withParameters(Values.parameters(
								"name", restaurant.getName(),
								"description", restaurant.getDescription(),
								"price", restaurant.getPrice().toString())));
				return true;
			});
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	};
	
	public static boolean registerUser(User user) {
		return registerUser(user, null);
	};
	
	
}
