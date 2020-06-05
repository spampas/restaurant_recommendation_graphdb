package ristogo.server;

import java.io.IOException;
import java.util.Properties;
import java.util.TimeZone;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import ristogo.server.storage.EntityManager;

public class RistogoServer
{
	private static int port = 8888;

	public static void main(String[] args)
	{
		Logger.getLogger(RistogoServer.class.getName()).entering(RistogoServer.class.getName(), "main", args);

		Options options = createOptions();
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
			parseOptions(cmd, options);
		} catch (ParseException ex) {
			Logger.getLogger(RistogoServer.class.getName()).warning("Can not parse command line options: " + ex.getMessage());
		}

		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

		startServer();
		Logger.getLogger(RistogoServer.class.getName()).exiting(RistogoServer.class.getName(), "main", args);
	}

	private static void startServer()
	{
		Logger.getLogger(RistogoServer.class.getName()).entering(RistogoServer.class.getName(), "startServer");
		ClientPool pool = null;
		try {
			Logger.getLogger(RistogoServer.class.getName()).info("Starting server...");
			pool = new ClientPool(port);
			Thread thread = new Thread(pool);
			thread.start();
			thread.join();
		} catch (IOException | InterruptedException ex) {
			Logger.getLogger(RistogoServer.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			Logger.getLogger(RistogoServer.class.getName()).info("Terminating...");
			EntityManager.closeFactory();
			if (pool != null)
				pool.shutdown();
			Logger.getLogger(RistogoServer.class.getName()).exiting(RistogoServer.class.getName(), "startServer");
		}
	}

	private static Options createOptions()
	{
		Options options = new Options();
		options.addOption(new Option("h", "help", false, "Print this message."));
		options.addOption(new Option("L", "leveldb", false, "Enable LevelDB database."));
		options.addOption(new Option("t", "print-leveldb", false, "Print LevelDB content and exit."));
		Option hostOpt = new Option("H", "host", true, "Specify MySQL database hostname (default: localhost).");
		hostOpt.setType(String.class);
		hostOpt.setArgName("HOST");
		options.addOption(hostOpt);
		Option dbportOpt = new Option("dbport", true, "Specify MySQL database port (default: 3306).");
		hostOpt.setType(Integer.class);
		hostOpt.setArgName("PORT");
		options.addOption(dbportOpt);
		Option userOpt = new Option("u", "user", true, "Specify MySQL database username (default: root).");
		userOpt.setType(String.class);
		userOpt.setArgName("USER");
		options.addOption(userOpt);
		Option passOpt = new Option("p", "pass", true, "Specify MySQL database password (default: <empty>).");
		passOpt.setType(String.class);
		passOpt.setArgName("PASS");
		options.addOption(passOpt);
		Option portOpt = new Option("P", "port", true, "Set listening port (default: 8888).");
		portOpt.setType(Integer.class);
		portOpt.setArgName("PORT");
		options.addOption(portOpt);
		Option logLevelOpt = new Option("l", "log-level", true, "Set log level.");
		logLevelOpt.setType(Level.class);
		logLevelOpt.setArgName("LEVEL");
		options.addOption(logLevelOpt);

		return options;
	}

	private static void parseOptions(CommandLine cmd, Options options)
	{
		if (cmd.hasOption("help")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("ristogoserver [-h | --help] [-H <HOST> | --host <HOST>] [--dbport <PORT>] [-u <USER> | --user <USER>] [-p <PASS> | --pass <PASS>] [-L | --leveldb] [-t | --print-leveldb] [-P <PORT> | --port <PORT>] [-l <LEVEL> | --log-level <LEVEL>]",
				"", options, "\nLOG LEVELS:\n" +
				"ALL: print all logs.\n" +
				"FINEST: print all tracing logs.\n" +
				"FINER: print most tracing logs.\n" +
				"FINE: print some tracing logs.\n" +
				"CONFIG: print all config logs.\n" +
				"INFO: print all informational logs.\n" +
				"WARNING: print all warnings and errors. (default)\n" +
				"SEVERE: print only errors.\n" +
				"OFF: disable all logs."
			);
			System.exit(0);
		}
		if (cmd.hasOption("log-level")) {
			String logLevelName = cmd.getOptionValue("log-level").toUpperCase();
			Level logLevel;
			try {
				logLevel = Level.parse(logLevelName);
			} catch (IllegalArgumentException ex) {
				Logger.getLogger(RistogoServer.class.getName()).warning("Invalid log level specified (" + logLevelName + "). Using default: WARNING.");
				logLevel = Level.WARNING;
			}
			setLogLevel(logLevel);
		}
		Properties properties = new Properties();
		String host = "localhost";
		String dbport = "3306";
		if (cmd.hasOption("host")) {
			host = cmd.getOptionValue("host");
			if (host.isBlank()) {
				Logger.getLogger(RistogoServer.class.getName()).warning("Invalid host specified. Using default: localhost.");
				host = "localhost";
			}
		}
		if (cmd.hasOption("dbport")) {
			dbport = cmd.getOptionValue("dbport");
			try {
				int intdbport = Integer.parseInt(dbport);
				if (intdbport < 0 || intdbport > 65535) {
					NumberFormatException ex = new NumberFormatException("The dbport must be a number between 0 and 65535.");
					Logger.getLogger(RistogoServer.class.getName()).throwing(RistogoServer.class.getName(), "parseOptions", ex);
					throw ex;
				}
			} catch (NumberFormatException ex) {
				Logger.getLogger(RistogoServer.class.getName()).warning("Invalid dbport specified. Using default: 3306.");
				dbport = "3306";
			}
		}
		properties.setProperty("javax.persistence.jdbc.url", "jdbc:mysql://" + host + ":" + dbport + "/ristogo?serverTimezone=UTC");
		if (cmd.hasOption("user")) {
			String user = cmd.getOptionValue("user");
			if (!user.isBlank())
				properties.setProperty("javax.persistence.jdbc.user", user);
			else
				Logger.getLogger(RistogoServer.class.getName()).warning("Invalid user specified. Using default: root.");
		}
		if (cmd.hasOption("pass")) {
			String pass = cmd.getOptionValue("pass");
			properties.setProperty("javax.persistence.jdbc.password", pass);
		}
		EntityManager.init(properties);
		if (cmd.hasOption("port")) {
			try {
				port = Integer.parseInt(cmd.getOptionValue("port", "8888"));
				if (port < 0 || port > 65535) {
					NumberFormatException ex = new NumberFormatException("The port must be a number between 0 and 65535.");
					Logger.getLogger(RistogoServer.class.getName()).throwing(RistogoServer.class.getName(), "parseOptions", ex);
					throw ex;
				}
			} catch (NumberFormatException ex) {
				Logger.getLogger(RistogoServer.class.getName()).warning("Invalid port specified. Using default: 8888.");
				port = 8888;
			}
		} else {
			Logger.getLogger(RistogoServer.class.getName()).config("Using default port 8888.");
			port = 8888;
		}
		if (cmd.hasOption("leveldb")) {
			Logger.getLogger(RistogoServer.class.getName()).config("Enabling LevelDB.");
			EntityManager.enableLevelDB();
		}
		if (cmd.hasOption("print-leveldb")) {
			Logger.getLogger(RistogoServer.class.getName()).info("Printing LevelDB content.");
			EntityManager.printKVDBContent();
			System.exit(0);
		}
	}

	private static void setLogLevel(Level level)
	{
		Logger rootLogger = LogManager.getLogManager().getLogger("");
		rootLogger.setLevel(level);
		for (Handler handler: rootLogger.getHandlers())
			handler.setLevel(level);

		Logger.getLogger(RistogoServer.class.getName()).config("Log level set to " + level + ".");
	}
}
