package net.treset.worldmanager;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.treset.worldmanager.commands.CommandHandler;
import net.treset.worldmanager.config.Config;
import net.treset.worldmanager.manager.ChunkManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class WorldManagerMod implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("worldmanager");

	private static final ChunkManager chunkManager = new ChunkManager();
	private static Config config;

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		try {
			config = Config.from(new File("config/worldmanager.json"));
		} catch (IOException e) {
			LOGGER.error("Failed to load config file", e);
			return;
		}

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> new CommandHandler().registerCommand(dispatcher, environment));

		LOGGER.info("Hello Fabric world!");
	}

	public static ChunkManager getChunkManager() {
		return chunkManager;
	}

	public static Config getConfig() {
		return config;
	}
}