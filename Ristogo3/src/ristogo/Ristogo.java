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

		launchGUI(args);

	}

	private static Options createOptions()
	{
		Options options = new Options();
		options.addOption(new Option("h", "help", false, "print this message."));
		Option serverAddress = new Option("H", "host", true, "server address");
		serverAddress.setType(String.class);
		serverAddress.setArgName("HOST");
		options.addOption(serverAddress);
		Option logLevelOpt = new Option("l", "log-level", true, "set log level.");
		logLevelOpt.setType(Level.class);
		logLevelOpt.setArgName("LEVEL");
		Option serverPort = new Option("p", "port", true, "server port" );
		serverPort.setType(Integer.class);
		serverPort.setArgName("PORT");
		options.addOption(logLevelOpt);

		return options;
	}

	private static void parseOptions(CommandLine cmd, Options options)
	{
		if (cmd.hasOption("help")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("ristogo [-h | --help] "
					+ "[-p <PORT>  | --port <PORT>]"
					+ "[-H <HOSTNAME>  | --host <HOSTNAME>]"
					+ "[-l <LEVEL> | --log-level <LEVEL>]",
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

		if(cmd.hasOption("host")) {
			if (cmd.hasOption("port")) {
				try {
					int port = Integer.parseInt(cmd.getOptionValue("port", "8888"));
					if (port < 0 || port > 65535) {
						NumberFormatException ex = new NumberFormatException("The port must be a number between 0 and 65535.");
						Logger.getLogger(Ristogo.class.getName()).throwing(Ristogo.class.getName(), "parseOptions", ex);
						throw ex;
					}
					Configuration.getConfig().setServerPort(port);
				} catch (NumberFormatException ex) {
					Logger.getLogger(Ristogo.class.getName()).warning("Invalid port specified. Using default: 8888.");
				}
			} else {
				Logger.getLogger(Ristogo.class.getName()).config("Using default port 8888.");
			}
		}
		if(cmd.hasOption("host")) {
			String host = cmd.getOptionValue("host");
				if(!host.isBlank())
					Configuration.getConfig().setServerIp(host);
		}
	}



	private static void launchGUI(String[] args)
	{
		Logger.getLogger(Ristogo.class.getName()).entering(Ristogo.class.getName(), "launchGUI", args);
		ristogo.ui.RistogoGUI.launch(args);
		Logger.getLogger(Ristogo.class.getName()).exiting(Ristogo.class.getName(), "launchGUI", args);
		close();
	}

	private static void close()
	{
		Logger.getLogger(Ristogo.class.getName()).fine("Exiting...");
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
