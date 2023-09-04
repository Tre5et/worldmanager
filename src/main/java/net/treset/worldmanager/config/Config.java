package net.treset.worldmanager.config;

import com.google.gson.Gson;

import java.io.*;
import java.util.ArrayList;

public class Config {
    private transient File file;
    private final ArrayList<Region> regions;

    public Config(File file) {
        this.file = file;
        regions = new ArrayList<>();
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

    public void setFile(File file) {
        this.file = file;
    }

    public static Config from(File file) throws IOException {
        if(file.exists()) {
            Gson gson = new Gson();
            Config config = gson.fromJson(new InputStreamReader(new FileInputStream(file)), Config.class);
            config.setFile(file);
            return config;
        }
        file.getParentFile().mkdirs();
        file.createNewFile();
        return new Config(file);
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
