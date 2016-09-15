package net.gameovr.biosphere;


import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraftforge.event.world.WorldEvent.CreateSpawnPosition;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BiosphereEventHandler {

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void onCreateSpawnPosition(CreateSpawnPosition event){

        BiosphereWorldType worldType = (BiosphereWorldType) event.getWorld().getWorldType();
        GameRules rules = event.getWorld().getGameRules();
        if(rules.hasRule("spawnRadius")){
            rules.setOrCreateGameRule("spawnRadius", "1");
            Biosphere.logger.info("Set spawnRadius to 1");
        }else{
            Biosphere.logger.info("Could NOT find Game Rule: spawnRadius");
        }

        BlockPos sphereOrigin = worldType.spheres.get(0).origin;
        BlockPos spawnPoint = new BlockPos(sphereOrigin.getX(), sphereOrigin.getY()-2, sphereOrigin.getZ());
        event.getWorld().getWorldInfo().setSpawn(spawnPoint);

        Biosphere.logger.info("Set Spawn Position: " + spawnPoint.getX() + "," + spawnPoint.getY() + "," + spawnPoint.getZ());

        BlockPos pos = event.getWorld().getSpawnPoint();

        event.setCanceled(true);

    }



}
