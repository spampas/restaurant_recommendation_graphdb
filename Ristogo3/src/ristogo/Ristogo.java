package ristogo;

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

import ristogo.config.Configuration;
import ristogo.ui.Console;
import ristogo.ui.menus.LoginMenu;

public class Ristogo
{
	public static void main(String[] args)
	{
		Logger.getLogger(Ristogo.class.getName()).entering(Ristogo.class.getName(), "main", args);

		Options options = createOptions();
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
			parseOptions(cmd, options);
		} catch (ParseException ex) {
			Logger.getLogger(Ristogo.class.getName()).warning("Can not parse command line options: " + ex.getMessage());
		}

		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		Configuration config = Configuration.getConfig();

		if (cmd == null || !cmd.hasOption("log-level"))
			setLogLevel(config.getLogLevel());

		switch (config.getInterfaceMode()) {
		case FORCE_CLI:
			Logger.getLogger(Ristogo.class.getName()).config("Forcing CLI by config option");
			launchCLI(args);
		case FORCE_GUI:
			Logger.getLogger(Ristogo.class.getName()).config("Forcing GUI by config option.");
			launchGUI(args);
		case AUTO:
			if (Console.exists()) {
				Logger.getLogger(Ristogo.class.getName()).config("Console found. Starting in CLI mode.");
				launchCLI(args);
			} else {
				Logger.getLogger(Ristogo.class.getName()).config("Console NOT found. Starting in GUI mode.");
				launchGUI(args);
			}
		}
	}

	private static Options createOptions()
	{
		Options options = new Options();
		options.addOption(new Option("g", "gui", false, "force load the Graphical User Interface."));
		options.addOption(new Option("c", "cli", false, "force load the Command Line Interface."));
		options.addOption(new Option("h", "help", false, "print this message."));
		Option logLevelOpt = new Option("l", "log-level", true, "set log level.");
		logLevelOpt.setType(Level.class);
		logLevelOpt.setArgName("LEVEL");
		options.addOption(logLevelOpt);

		return options;
	}

	private static void parseOptions(CommandLine cmd, Options options)
	{
		if (cmd.hasOption("help")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("ristogo [-h | --help] [-c | --cli] [-g | --gui] [-l <LEVEL> | --log-level <LEVEL>]",
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
			close();
		}
		if (cmd.hasOption("log-level")) {
			String logLevelName = cmd.getOptionValue("log-level").toUpperCase();
			Level logLevel;
			try {
				logLevel = Level.parse(logLevelName);
			} catch (IllegalArgumentException ex) {
				Logger.getLogger(Ristogo.class.getName()).warning("Invalid log level specified (" + logLevelName + "). Using default: WARNING.");
				logLevel = Level.WARNING;
			}
			setLogLevel(logLevel);
		}
		if (cmd.hasOption("cli")) {
			Logger.getLogger(Ristogo.class.getName()).config("Forcing CLI by command line argument.");
			launchCLI(cmd.getArgs());
		}
		if (cmd.hasOption("gui")) {
			Logger.getLogger(Ristogo.class.getName()).config("Forcing GUI by command line argument.");
			launchGUI(cmd.getArgs());
		}
	}

	private static void launchCLI(String[] args)
	{
		Logger.getLogger(Ristogo.class.getName()).entering(Ristogo.class.getName(), "launchCLI", args);
		Console.println("WELCOME TO RISTOGO!");

		new LoginMenu().show();

		Logger.getLogger(Ristogo.class.getName()).exiting(Ristogo.class.getName(), "launchCLI", args);
		close();
	}

	private static void launchGUI(String[] args)
	{
		Logger.getLogger(Ristogo.class.getName()).entering(Ristogo.class.getName(), "launchGUI", args);
		ristogo.ui.graphics.RistogoGUI.launch(args);
		Logger.getLogger(Ristogo.class.getName()).exiting(Ristogo.class.getName(), "launchGUI", args);
		close();
	}

	private static void close()
	{
		Logger.getLogger(Ristogo.class.getName()).fine("Exiting...");
		Console.close();
		System.exit(0);
	}

	private static void setLogLevel(Level level)
	{
		Logger rootLogger = LogManager.getLogManager().getLogger("");
		rootLogger.setLevel(level);
		for (Handler handler: rootLogger.getHandlers())
			handler.setLevel(level);

		Logger.getLogger(Ristogo.class.getName()).config("Log level set to " + level + ".");
	}
}
