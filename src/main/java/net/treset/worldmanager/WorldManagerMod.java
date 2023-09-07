package net.treset.worldmanager;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.treset.worldmanager.commands.CommandHandler;
import net.treset.worldmanager.config.Config;
import net.treset.worldmanager.manager.ChunkManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;

public class WorldManagerMod implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("worldmanager");

	private static final ChunkManager chunkManager = new ChunkManager();

	private static String levelName = "world";
	private static final ArrayList<Config> configs = new ArrayList<>();

	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> new CommandHandler().registerCommand(dispatcher, environment));
		loadWorldName();
	}

	private static void loadWorldName() {
		File serverPropertiesFile = new File("server.properties");
		BufferedReader serverPropertiesReader;
		try {
			serverPropertiesReader = new BufferedReader(new InputStreamReader(new FileInputStream(serverPropertiesFile)));
		} catch (FileNotFoundException e) {
			LOGGER.error("Failed to find server.properties file.");
			return;
		}

		String line;
		while(true) {
			try {
				if ((line = serverPropertiesReader.readLine()) == null) break;
				if(line.startsWith("level-name=")) {
					levelName = line.substring(11);
					break;
				}
			} catch (IOException e) {
				LOGGER.error("Failed to read server.properties file.");
				return;
			}
		}
	}

	public static ChunkManager getChunkManager() {
		return chunkManager;
	}

	public static String getLevelName() {
		return levelName;
	}

	public static Config getConfig(String dimensionId) {
		for(Config config : configs) {
			if(config.getDimensionId().equals(dimensionId)) {
				return config;
			}
		}

		Config config;
		try {
			config = Config.from(dimensionId);
		} catch (IOException e) {
			LOGGER.error("Failed to load config file.", e);
			return null;
		}
		configs.add(config);
		return config;
	}
}