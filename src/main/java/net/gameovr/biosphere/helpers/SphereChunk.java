package net.gameovr.biosphere.helpers;

import net.gameovr.biosphere.Sphere;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

public class SphereChunk {

    public ArrayList<BlockPos> blocks;
    public Sphere nearestSphere;
    public boolean isSphereInChunk = false;


    public SphereChunk(ArrayList<BlockPos> blocksIn, Sphere sphere, boolean isInChunk){
        blocks = blocksIn;
        nearestSphere = sphere;
        isSphereInChunk = isInChunk;
    }

    public SphereChunk(){

    }

}
