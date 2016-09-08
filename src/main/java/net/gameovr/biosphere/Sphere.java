package net.gameovr.biosphere;

import net.minecraft.util.math.BlockPos;

public class Sphere {

    public BlockPos origin;
    public int radius;

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
        int x1 = origin.getX();
        int y1 = origin.getY();
        int z1 = origin.getZ();
        int x2 = blockX;
        int y2 = blockY;
        int z2 = blockZ;
        return (int)Math.round(Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1) + (z2 - z1) * (z2 - z1)));
    }

    public BlockPos getOrigin() {
        return origin;
    }

    public void setOrigin(BlockPos origin) {
        this.origin = origin;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }
}
