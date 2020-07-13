package ristogo.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.thoughtworks.xstream.XStream;

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
}
