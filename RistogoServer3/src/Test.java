import org.neo4j.driver.Session;

import ristogo.common.entities.City;
import ristogo.common.entities.Cuisine;
import ristogo.common.entities.Customer;
import ristogo.common.entities.Restaurant;
import ristogo.common.entities.User;
import ristogo.common.entities.enums.Price;
import ristogo.db.DBManager;

public class Test {

	public static void main(String[] args)
	{
		try (Session session = DBManager.getInstance().getSession()){
			Customer u = new Customer("Nico", "nicolapass");
			Restaurant r = new Restaurant();
			Cuisine c = new Cuisine("Pizza");
			
			r.setCuisine(new Cuisine("Pizza"));
			r.setName("Pizzeria da Pino");
			r.setDescription("Pizza buonissima. prezzi anche");
			r.setPrice(Price.ECONOMIC);
			
			session.writeTransaction(tx -> DBManager.dislike(tx,u,c));
			session.writeTransaction(tx -> DBManager.dislike(tx, u, r));
			session.writeTransaction(tx -> DBManager.unserve(tx, r, c));
			
		} catch(Exception e) {
			e.printStackTrace();
		}

	}

}
