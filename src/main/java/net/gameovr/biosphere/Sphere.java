package net.gameovr.biosphere;

import net.gameovr.biosphere.helpers.ChunkCoordinate;
import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.Biome;
import scala.tools.nsc.doc.base.comment.Block;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Sphere {

    public BlockPos origin;
    public int radius;
    public Biome biome = Biomes.DEFAULT;
    public ChunkCoordinate originChunk;
    private boolean hasLake = false;
    public BlockPos startBridgeConnection;
    public BlockPos endBridgeConnection;
    public BlockPos[] Connections = new BlockPos[4];
    public Vec3d bridgeConnection;
    public ArrayList<BlockPos> bridgeBlocks;

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
        Connections[0] = origin.north(radius).down(3);
        Connections[1] = origin.east(radius).down(3);
        Connections[2] = origin.south(radius).down(3);
        Connections[3] = origin.west(radius).down(3);
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

    public BlockPos getOriginAtGroundLevel(){return origin.add(0, origin.getY() - getSphereGroundLevel(), 0);}

    public void setHasLake(){
        hasLake = true;
    }
    public boolean getHasLake(){
        return hasLake;
    }

    @Override
    public String toString() {
        String rtn = null;
        String origin = "Origin: " + this.origin.getX() + ", " + this.origin.getY() + ", " + this.origin.getZ();
if(startBridgeConnection != null && endBridgeConnection != null) {
    rtn = origin.concat("  Start Bridge:" + this.startBridgeConnection.toString() + " End Bridge: " + this.endBridgeConnection.toString());
}
else{
    rtn = origin;
}
        return rtn;
    }
}
