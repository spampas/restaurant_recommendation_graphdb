package ristogo.server.db.entities;

import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class Admin extends User // used just for admins count on first start
{
	public Admin()
	{
		super();
	}

	public Admin(String username, String password, City city)
	{
		super(username, password, city);
	}
	
	public boolean isAdmin() {
		return true;
	} 
}
