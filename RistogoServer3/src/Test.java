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
			Restaurant r = new Restaurant();
			r.setCuisine(new Cuisine("Pizza"));
			r.setName("Pizzeria da Pino");
			r.setDescription("Pizza buonissima. prezzi anche");
			r.setPrice(Price.ECONOMIC);
			session.writeTransaction( tx -> DBManager.delete(tx, r) );
			session.writeTransaction( tx -> DBManager.addRestaurant(tx,r));
			session.writeTransaction( tx -> DBManager.serve(tx,r,new Cuisine("Pizza")));
		} catch(Exception e) {
			e.printStackTrace();
		}

	}

}
