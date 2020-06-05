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

	private String serverIp;
	private int serverPort;
	private InterfaceMode interfaceMode;
	private String fontName;
	private int fontSize;
	private String bgColorName;
	private String fgColorName;
	private int numberRowsDisplayable;
	private Level logLevel;

	private Configuration()
	{
		mergeDefaults();
	}

	private void mergeDefaults()
	{
		if (serverIp == null || serverIp.isBlank()) {
			serverIp = "127.0.0.1";
			Logger.getLogger(Configuration.class.getName()).config("serverIp not set. Using default value: " + serverIp + ".");
		}
		if (serverPort == 0) {
			serverPort = 8888;
			Logger.getLogger(Configuration.class.getName()).config("serverPort not set. Using default value: " + serverPort + ".");
		}
		if (interfaceMode == null) {
			interfaceMode = InterfaceMode.AUTO;
			Logger.getLogger(Configuration.class.getName()).config("interfaceMode not set. Using default value: " + interfaceMode + ".");
		}
		if (fontName == null || fontName.isBlank()) {
			fontName = "Open Sans";
			Logger.getLogger(Configuration.class.getName()).config("fontName not set. Using default value: " + fontName + ".");
		}
		if (fontSize == 0) {
			fontSize = 14;
			Logger.getLogger(Configuration.class.getName()).config("fontSize not set. Using default value: " + fontSize + ".");
		}
		if (bgColorName == null || bgColorName.isBlank()) {
			bgColorName = "FFFFFF";
			Logger.getLogger(Configuration.class.getName()).config("bgColorName not set. Using default value: " + bgColorName + ".");
		}
		if (fgColorName == null || fgColorName.isBlank()) {
			fgColorName = "D9561D";
			Logger.getLogger(Configuration.class.getName()).config("fgColorName not set. Using default value: " + fgColorName + ".");
		}
		if (numberRowsDisplayable == 0) {
			numberRowsDisplayable = 7;
			Logger.getLogger(Configuration.class.getName()).config("numberRowsDisplayable not set. Using default value: " + numberRowsDisplayable + ".");
		}
		if (logLevel == null) {
			logLevel = Level.WARNING;
			Logger.getLogger(Configuration.class.getName()).config("logLevel not set. Using default value: " + logLevel + ".");
		}
	}

	public static Configuration getConfig()
	{
		if (singletonObj == null)
			try {
				singletonObj = loadConfiguration();
			} catch (FileNotFoundException | InvalidConfigurationException ex) {
				if (ex instanceof FileNotFoundException)
					Logger.getLogger(Configuration.class.getName()).config("Can not find configuration file. Loading defaults...");
				else if (ex instanceof InvalidConfigurationException)
					Logger.getLogger(Configuration.class.getName()).config("Invalid configuration file. Loading defaults...");
				singletonObj = new Configuration();
			}
		return singletonObj;
	}

	private static Configuration loadConfiguration() throws FileNotFoundException, InvalidConfigurationException
	{
		if (!validateConfiguration()) {
			InvalidConfigurationException ex = new InvalidConfigurationException();
			Logger.getLogger(Configuration.class.getName()).throwing(Configuration.class.getName(), "loadConfiguration", ex);
			throw ex;
		}
		XStream xs = new XStream();
		FileReader fr = new FileReader("config.xml");
		xs.alias("Configuration", Configuration.class);
		Configuration config = (Configuration)xs.fromXML(fr);
		Logger.getLogger(Configuration.class.getName()).config("Configuration loaded from config.xml");
		config.mergeDefaults();
		return config;
	}

	private static boolean validateConfiguration()
	{
		try {
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Document d = db.parse(new File("config.xml"));
			Schema s = sf.newSchema(new StreamSource(Configuration.class.getResourceAsStream("/resources/config.xsd")));
			s.newValidator().validate(new DOMSource(d));
		} catch (SAXException | ParserConfigurationException | IOException ex) {
			Logger.getLogger(Configuration.class.getName()).log(Level.CONFIG, "Error during config file validation.", ex);
			return false;
		}
		return true;
	}

	public String getServerIp()
	{
		return serverIp;
	}

	public int getServerPort()
	{
		return serverPort;
	}

	public InterfaceMode getInterfaceMode()
	{
		return interfaceMode;
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
