package net.treset.worldmanager.config;

import java.util.ArrayList;

public class Region {
    private final int x;
    private final int z;
    private final ArrayList<ArrayList<Chunk>> chunks;

    public Region(int x, int z, ArrayList<ArrayList<Chunk>> chunks) {
        this.x = x;
        this.z = z;
        this.chunks = chunks;
    }

    public ArrayList<ArrayList<Chunk>> getChunks() {
        return chunks;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }
}
