package net.treset.worldmanager.config;

import com.google.gson.Gson;
import net.treset.worldmanager.WorldManagerMod;
import net.treset.worldmanager.manager.CommandCallback;

import java.io.*;
import java.util.ArrayList;

public class Config {
    private transient File file;
    private transient String dimensionId;
    private final ArrayList<Region> regions;

    public Config(String dimensionId) {
        setDimensionId(dimensionId);
        this.regions = new ArrayList<>();
    }

    public ArrayList<Region> getRegions() {
        return regions;
    }

    public Region getRegion(int x, int z) {
        for(Region region : regions) {
            if(region.getX() == x && region.getZ() == z) {
                return region;
            }
        }

        Region region = new Region(x, z, getNewChunks());
        regions.add(region);
        return region;
    }

    public CommandCallback exportMcaSelector() {
        StringBuilder out = new StringBuilder();
        for(Region region : regions) {
            for(ArrayList<Chunk> chunks : region.getChunks()) {
                for(Chunk chunk : chunks) {
                    if(chunk != null) {
                        out.append(region.getX()).append(";").append(region.getZ()).append(";").append(32 * region.getX() + chunk.getChunkX()).append(";").append(32 * region.getZ() + chunk.getChunkZ()).append("\n");
                    }
                }
            }
        }
        File outFile = new File("worldmanager/" + WorldManagerMod.getLevelName() + "." + dimensionId + "-mcaselector.csv");
        if(!outFile.exists()) {
            try {
                outFile.getParentFile().mkdirs();
                outFile.createNewFile();
            } catch (IOException e) {
                return new CommandCallback(CommandCallback.Type.FAILURE, "Failed to create file.");
            }
        }

        try {
            FileWriter writer = new FileWriter(outFile);
            writer.write(out.toString());
            writer.close();
        } catch (IOException e) {
            return new CommandCallback(CommandCallback.Type.FAILURE, "Failed to write to file.");
        }
        return new CommandCallback(CommandCallback.Type.SUCCESS, "Successfully exported to worldmanager/" + WorldManagerMod.getLevelName() + "." + dimensionId + "-mcaselector.csv");
    }

    public void save() throws IOException {
        Gson gson = new Gson();
        String json = gson.toJson(this);
        FileWriter writer = new FileWriter(file);
        writer.write(json);
        writer.close();
    }

    public File getFile() {
        return file;
    }

    public String getDimensionId() {
        return dimensionId;
    }

    public void setDimensionId(String dimensionId) {
        this.dimensionId = dimensionId;
        this.file = new File(WorldManagerMod.getLevelName() + "/worldmanager/data/" + dimensionId + ".json");
    }

    public static Config from(String dimensionId) throws IOException {
        File file = new File(WorldManagerMod.getLevelName() + "/worldmanager/data/" + dimensionId + ".json");

        if(file.exists()) {
            Gson gson = new Gson();
            Config config = gson.fromJson(new InputStreamReader(new FileInputStream(file)), Config.class);
            config.setDimensionId(dimensionId);
            return config;
        }
        file.getParentFile().mkdirs();
        file.createNewFile();
        return new Config(dimensionId);
    }

    public static ArrayList<ArrayList<Chunk>> getNewChunks() {
        ArrayList<ArrayList<Chunk>> chunks = new ArrayList<>();
        for(int i = 0; i < 32; i++) {
            chunks.add(new ArrayList<>());
            for(int j = 0; j < 32; j++) {
                chunks.get(i).add(null);
            }
        }
        return chunks;
    }
}
