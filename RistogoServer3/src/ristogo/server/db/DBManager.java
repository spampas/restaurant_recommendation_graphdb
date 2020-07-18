package ristogo.server.db;

import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;

public class DBManager
{
	private static ThreadLocal<DBManager> instance = new ThreadLocal<DBManager>();

	private static String username = "neo4j";
	private static String password = "password";
	private static String host = "localhost";
	private static int port = 7687;
	private static String databaseName;

	private static SessionFactory factory;

	private Session session;

	private DBManager()
	{
		initFactory();
	}

	private static synchronized final void initFactory()
	{
		if (factory != null)
			return;
		Configuration configuration = new Configuration.Builder()
			.uri("bolt://" + host + ":" + port)
			.credentials(username, password)
			.database(databaseName)
			.connectionPoolSize(150)
			.build();
		factory = new SessionFactory(configuration, "ristogo.server.db.entities");
	}

	public static DBManager getInstance()
	{
		if (instance.get() == null)
			instance.set(new DBManager());
		return instance.get();
	}

	public Session getSession()
	{
		if (session == null)
			session = factory.openSession();
		return session;
	}

	public static Session session()
	{
		return getInstance().getSession();
	}

	public static void setHost(String host)
	{
		if (host == null || host.isBlank())
			host = "localhost";
		DBManager.host = host;
	}

	public static void setPort(int port)
	{
		if (port < 0 || port > 65535)
			throw new IllegalArgumentException("Invalid port specified.");
		DBManager.port = port;
	}

	public static void setUsername(String username)
	{
		if (username == null || username.isBlank())
			username = "neo4j";
		DBManager.username = username;
	}

	public static void setPassword(String password)
	{
		DBManager.password = password;
	}

	public static void setDatabaseName(String databaseName)
	{
		if (databaseName != null && databaseName.isEmpty())
			databaseName = null;
		DBManager.databaseName = databaseName;
	}

	public void close()
	{
		if (session == null)
			return;
		session.clear();
		session = null;
	}

	public static void dispose()
	{
		if (instance.get() != null)
			instance.get().close();
		if (factory == null)
			return;
		factory.close();
	}
}
