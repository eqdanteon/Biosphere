package net.gameovr.biosphere;

import net.gameovr.biosphere.helpers.ChunkCoordinate;
import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

public class Sphere {

    public BlockPos origin;
    public int radius;
    public Biome biome = Biomes.DEFAULT;
    public ChunkCoordinate originChunk;
    private boolean hasLake = false;
    public BlockPos startBridgeConnection;
    public BlockPos endBridgeConnection;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Sphere sphere = (Sphere) o;

        if (getRadius() != sphere.getRadius()) return false;
        if (!getOrigin().equals(sphere.getOrigin())) return false;
        return originChunk.equals(sphere.originChunk);

    }

    @Override
    public int hashCode() {
        int result = getOrigin().hashCode();
        result = 31 * result + getRadius();
        result = 31 * result + originChunk.hashCode();
        return result;
    }

    public Sphere(BlockPos origin, int radius, ChunkCoordinate chunk) {
        this.origin = origin;
        this.radius = radius;
        this.originChunk = chunk;
    }

    public int getDistanceFromOrigin(BlockPos blockpos) {
        int x1 = origin.getX();
        int y1 = origin.getY();
        int z1 = origin.getZ();
        int x2 = blockpos.getX();
        int y2 = blockpos.getY();
        int z2 = blockpos.getZ();
        return (int)Math.round(Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1) + (z2 - z1) * (z2 - z1)));
    }

    public int getDistanceFromOrigin(int blockX, int blockY, int blockZ) {
        return getDistanceFromOrigin(new BlockPos(blockX, blockY, blockZ));
    }

    public BlockPos getOrigin() {
        return origin;
    }

    public int getRadius() {
        return radius;
    }

    public int getSphereGroundLevel(){return origin.getY() - 2;}

    public void setHasLake(){
        hasLake = true;
    }
    public boolean getHasLake(){
        return hasLake;
    }

    @Override
    public String toString() {
        String origin = "Origin: " + this.origin.getX() + ", " + this.origin.getY() + ", " + this.origin.getZ();

        //String bridge = "  Bridge:" + this.startBridgeConnection.toString();
        return origin;
    }
}
