package net.treset.worldmanager.config;

public class Chunk {
    private int regionX;
    private int regionZ;
    private int chunkX;
    private int chunkZ;

    public Chunk(int regionX, int regionZ, int chunkX, int chunkZ) {
        this.regionX = regionX;
        this.regionZ = regionZ;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
    }

    public int getRegionX() {
        return regionX;
    }

    public void setRegionX(int regionX) {
        this.regionX = regionX;
    }

    public int getRegionZ() {
        return regionZ;
    }

    public void setRegionZ(int regionZ) {
        this.regionZ = regionZ;
    }

    public int getChunkX() {
        return chunkX;
    }

    public void setChunkX(int chunkX) {
        this.chunkX = chunkX;
    }

    public int getChunkZ() {
        return chunkZ;
    }

    public void setChunkZ(int chunkZ) {
        this.chunkZ = chunkZ;
    }
}
