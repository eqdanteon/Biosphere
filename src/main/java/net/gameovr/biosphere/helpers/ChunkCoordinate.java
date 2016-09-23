package net.gameovr.biosphere.helpers;

public class ChunkCoordinate {

    public int x;
    public int y;
    public int z;

    public ChunkCoordinate(int chunkX, int subchunkY, int chunkZ){
        x = chunkX;
        y = subchunkY;
        z = chunkZ;
    }

}
