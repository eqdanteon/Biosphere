package net.gameovr.biosphere;

import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

public class Sphere {

    public static final int MAX_BRIDGE_CONNECTIONS = 3;
    public BlockPos origin;
    public int radius;
    public static final ArrayList<BlockPos> bridgeConnections = new ArrayList<BlockPos>(MAX_BRIDGE_CONNECTIONS);



    public Sphere(BlockPos origin, int radius) {
        this.origin = origin;
        this.radius = radius;
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

    @Override
    public String toString() {
        return "Origin: " + this.origin.getX() + ", " + this.origin.getY() + ", " + this.origin.getZ();
    }
}
