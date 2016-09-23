package net.gameovr.biosphere;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.WorldProviderSurface;

public class BiosphereWorldProvider extends WorldProviderSurface {


    @Override
    public BlockPos getSpawnCoordinate() {
        return setBiosphereSpawnPoint();
    }

    @Override
    public BlockPos getRandomizedSpawnPoint() {
        return setBiosphereSpawnPoint();
    }

    @Override
    public boolean canCoordinateBeSpawn(int x, int z) {
        for(int y = 0; y < 256; y++){

            if(!worldObj.isAirBlock(new BlockPos(x,y,z))){
                return true;
            }

        }

        return false;
    }

    @Override
    public boolean canRespawnHere() {
        return false;
    }

    public BlockPos setBiosphereSpawnPoint(){

        GameRules rules = this.worldObj.getGameRules();
        if(rules.hasRule("spawnRadius")){
            System.out.println("Found Game Rule: spawnRadius");
            rules.setOrCreateGameRule("spawnRadius", "1");
            System.out.println("Set spawnRadius to 1");
        }else{
            System.out.println("Could NOT find Game Rule: spawnRadius");
        }

        return BiosphereWorldType.spawnPoint;
    }

}
