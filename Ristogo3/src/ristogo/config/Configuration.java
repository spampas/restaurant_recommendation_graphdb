package ristogo.config;

import java.io.Serializable;
import java.util.logging.Level;

public class Configuration implements Serializable
{
	private static final long serialVersionUID = 3785622852095732177L;

	private static Configuration singletonObj;

	private String serverIp = "localhost";
	private int serverPort = 8888;
	private String fontName = "Open Sans";
	private int fontSize = 14;
	private String bgColorName = "FFFFFF";
	private String fgColorName = "D9561D";
	private int numberRowsDisplayable = 7;
	private Level logLevel = Level.WARNING;

	private Configuration()
	{
	}

	public static Configuration getConfig()
	{
		if (singletonObj == null)
			singletonObj = new Configuration();
		return singletonObj;
	}

	public String getServerIp()
	{
		return serverIp;
	}

	public int getServerPort()
	{
		return serverPort;
	}


	public String getFontName()
	{
		return fontName;
	}

	public double getFontSize()
	{
		return fontSize;
	}

	public String getBgColorName()
	{
		return bgColorName;
	}

	public int getNumberRowsDisplayable()
	{
		return numberRowsDisplayable;
	}

	public String getFgColorName()
	{
		return fgColorName;
	}

	public Level getLogLevel()
	{
		return logLevel;
	}

	public void setServerIp(String serverIp)
	{
		this.serverIp = serverIp;
	}

	public void setServerPort(int serverPort)
	{
		this.serverPort = serverPort;
	}
}
