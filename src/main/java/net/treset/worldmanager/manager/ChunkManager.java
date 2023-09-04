package net.treset.worldmanager.manager;

import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.treset.worldmanager.WorldManagerMod;
import net.treset.worldmanager.config.Chunk;
import net.treset.worldmanager.config.Config;
import net.treset.worldmanager.config.Region;

import java.io.IOException;

public class ChunkManager {
    public CommandCallback add(Vec3d origin, int radius) {
        Config config = WorldManagerMod.getConfig();
        int chunkX = (int)Math.floor(origin.getX()) / 16;
        int chunkZ = (int)Math.floor(origin.getZ()) / 16;

        int count = 0;
        for(int i = -radius; i <= radius; i++) {
            for(int j = -radius; j <= radius; j++) {
                int regionX = (chunkX + i) >> 5;
                int regionZ = (chunkZ + j) >> 5;
                Region region = config.getRegion(regionX, regionZ);
                int chunkXInRegion = (((chunkX + i) % 32) + 32) % 32;
                int chunkZInRegion = (((chunkZ + j) % 32) + 32) % 32;
                if(region.getChunks().get(chunkXInRegion).get(chunkZInRegion) == null) {
                    count++;
                    region.getChunks().get(chunkXInRegion).set(chunkZInRegion, new Chunk(chunkXInRegion, chunkZInRegion));
                }
            }
        }
        if(count > 0) {
            try {
                WorldManagerMod.getConfig().save();
            } catch (IOException e) {
                WorldManagerMod.LOGGER.error("Failed to save config file.", e);
                return new CommandCallback(CommandCallback.Type.FAILURE, "Failed to save config file.");
            }
        }
        return new CommandCallback(CommandCallback.Type.SUCCESS, "Successfully added " + count + " chunks.");
    }

    public CommandCallback add(Vec2f pos1, Vec2f pos2) {
        int chunkX1 = (int)Math.floor(pos1.x) / 16;
        int chunkZ1 = (int)Math.floor(pos1.y) / 16;
        int chunkX2 = (int)Math.floor(pos2.x) / 16;
        int chunkZ2 = (int)Math.floor(pos2.y) / 16;
        if(chunkX1 > chunkX2) {
            int temp = chunkX1;
            chunkX1 = chunkX2;
            chunkX2 = temp;
        }
        if(chunkZ1 > chunkZ2) {
            int temp = chunkZ1;
            chunkZ1 = chunkZ2;
            chunkZ2 = temp;
        }

        int count = 0;
        for(int i = chunkX1; i <= chunkX2; i++) {
            for(int j = chunkZ1; j <= chunkZ2; j++) {
                int regionX = i >> 5;
                int regionZ = j >> 5;
                Region region = WorldManagerMod.getConfig().getRegion(regionX, regionZ);
                int chunkXInRegion = ((i % 32) + 32) % 32;
                int chunkZInRegion = ((j % 32) + 32) % 32;
                if(region.getChunks().get(chunkXInRegion).get(chunkZInRegion) == null) {
                    region.getChunks().get(chunkXInRegion).set(chunkZInRegion, new Chunk(chunkXInRegion, chunkZInRegion));
                    count++;
                }
            }
        }
        if(count > 0) {
            try {
                WorldManagerMod.getConfig().save();
            } catch (IOException e) {
                WorldManagerMod.LOGGER.error("Failed to save config file.", e);
                return new CommandCallback(CommandCallback.Type.FAILURE, "Failed to save config file.");
            }
        }
        return new CommandCallback(CommandCallback.Type.SUCCESS, "Successfully added " + count + " chunks.");
    }

    public CommandCallback remove(Vec3d origin, int radius) {
        Config config = WorldManagerMod.getConfig();
        int chunkX = (int)Math.floor(origin.getX()) / 16;
        int chunkZ = (int)Math.floor(origin.getZ()) / 16;

        int count = 0;
        for(int i = -radius; i <= radius; i++) {
            for(int j = -radius; j <= radius; j++) {
                int regionX = (chunkX + i) >> 5;
                int regionZ = (chunkZ + j) >> 5;
                Region region = config.getRegion(regionX, regionZ);
                int chunkXInRegion = (((chunkX + i) % 32) + 32) % 32;
                int chunkZInRegion = (((chunkZ + j) % 32) + 32) % 32;
                if(region.getChunks().get(chunkXInRegion).get(chunkZInRegion) != null) {
                    count++;
                    region.getChunks().get(chunkXInRegion).set(chunkZInRegion, null);
                }
            }
        }
        if(count > 0) {
            try {
                WorldManagerMod.getConfig().save();
            } catch (IOException e) {
                WorldManagerMod.LOGGER.error("Failed to save config file.", e);
                return new CommandCallback(CommandCallback.Type.FAILURE, "Failed to save config file.");
            }
        }
        return new CommandCallback(CommandCallback.Type.SUCCESS, "Successfully removed " + count + " chunks.");
    }

    public CommandCallback remove(Vec2f pos1, Vec2f pos2) {
        int chunkX1 = (int)Math.floor(pos1.x) / 16;
        int chunkZ1 = (int)Math.floor(pos1.y) / 16;
        int chunkX2 = (int)Math.floor(pos2.x) / 16;
        int chunkZ2 = (int)Math.floor(pos2.y) / 16;
        if(chunkX1 > chunkX2) {
            int temp = chunkX1;
            chunkX1 = chunkX2;
            chunkX2 = temp;
        }
        if(chunkZ1 > chunkZ2) {
            int temp = chunkZ1;
            chunkZ1 = chunkZ2;
            chunkZ2 = temp;
        }

        int count = 0;
        for(int i = chunkX1; i <= chunkX2; i++) {
            for(int j = chunkZ1; j <= chunkZ2; j++) {
                int regionX = i >> 5;
                int regionZ = j >> 5;
                Region region = WorldManagerMod.getConfig().getRegion(regionX, regionZ);
                int chunkXInRegion = ((i % 32) + 32) % 32;
                int chunkZInRegion = ((j % 32) + 32) % 32;
                if(region.getChunks().get(chunkXInRegion).get(chunkZInRegion) != null) {
                    region.getChunks().get(chunkXInRegion).set(chunkZInRegion, null);
                    count++;
                }
            }
        }
        if(count > 0) {
            try {
                WorldManagerMod.getConfig().save();
            } catch (IOException e) {
                WorldManagerMod.LOGGER.error("Failed to save config file.", e);
                return new CommandCallback(CommandCallback.Type.FAILURE, "Failed to save config file.");
            }
        }
        return new CommandCallback(CommandCallback.Type.SUCCESS, "Successfully removed " + count + " chunks.");
    }
}
