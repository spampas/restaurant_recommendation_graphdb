package ristogo.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
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

import ristogo.server.db.DBManager;
import ristogo.server.db.entities.Admin;
import ristogo.server.db.entities.User;

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

		createAdmin();
		startServer();

		Logger.getLogger(RistogoServer.class.getName()).exiting(RistogoServer.class.getName(), "main", args);
	}

	private static void createAdmin()
	{
		boolean hasAdmin = DBManager.session().countEntitiesOfType(Admin.class) > 0;
		if (hasAdmin)
			return;
		Scanner scanner = System.console() != null ?
		new Scanner(System.console().reader()) : new Scanner(System.in);
		System.out.println("*** CREATE ADMIN USER ***");
		System.out.println();
		String username = null;
		while (username == null) {
			System.out.print("USERNAME [admin]: ");
			System.out.flush();
			username = scanner.nextLine();
			username.trim();
			if (username.isBlank()) {
				username = "admin";
				break;
			}
			if (!username.matches("^[A-Za-z0-9]{3,32}$")) {
				System.out.println("Invalid username.");
				username = null;
				continue;
			}
		}
		String password = null;
		while (password == null) {
			System.out.print("PASSWORD: ");
			System.out.flush();
			if (System.console() == null)
				password = scanner.nextLine();
			else
				password = new String(System.console().readPassword());
			if (password.isBlank()) {
				System.out.println("Invalid password.");
				password = null;
				continue;
			}
			if (password.length() < 8) {
				System.out.println("Password must be at least 8 chars long.");
				password = null;
				continue;
			}
		}
		User admin = new User();
		admin.setUsername(username);
		admin.setPassword(password);
		admin.promote();
		DBManager.session().save(admin);
		System.out.println();
		System.out.println("*** ADMIN '" + username + "' CREATED ***");
		System.out.println();
	}

	private static void startServer()
	{
		Logger.getLogger(RistogoServer.class.getName()).entering(RistogoServer.class.getName(), "startServer");
		RequestHandlerPool pool = null;
		try {
			Logger.getLogger(RistogoServer.class.getName()).info("Starting server...");
			pool = new RequestHandlerPool(port);
			Thread thread = new Thread(pool);
			thread.start();
			thread.join();
		} catch (IOException | InterruptedException ex) {
			Logger.getLogger(RistogoServer.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			Logger.getLogger(RistogoServer.class.getName()).info("Terminating...");
			if (pool != null)
				pool.shutdown();
			DBManager.dispose();
			Logger.getLogger(RistogoServer.class.getName()).exiting(RistogoServer.class.getName(), "startServer");
		}
	}

	private static Options createOptions()
	{
		Options options = new Options();
		options.addOption(new Option("h", "help", false, "Print this message."));
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
			formatter.printHelp("ristogoserver [-h | --help] [-P <PORT> | --port <PORT>] [-l <LEVEL> | --log-level <LEVEL>]",
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
