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

    public BlockPos setBiosphereSpawnPoint(){

        BiosphereWorldType worldType = (BiosphereWorldType) this.worldObj.getWorldType();

        GameRules rules = this.worldObj.getGameRules();
        if(rules.hasRule("spawnRadius")){
            System.out.println("Found Game Rule: spawnRadius");
            rules.setOrCreateGameRule("spawnRadius", "1");
            System.out.println("Set spawnRadius to 1");
        }else{
            System.out.println("Could NOT find Game Rule: spawnRadius");
        }

        //int getSphereIndex = new Random(event.getWorld().getSeed()).nextInt(worldType.spheres.size());
        BlockPos sphereOrigin = worldType.spheres.get(0).origin;

        BlockPos spawnPoint = new BlockPos(sphereOrigin.getX(), sphereOrigin.getY()-2, sphereOrigin.getZ());

        return spawnPoint;
    }

}
