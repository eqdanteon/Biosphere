package net.gameovr.biosphere;


import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.event.terraingen.BiomeEvent;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BiosphereEventHandler {

    public void onDecorateEvent(DecorateBiomeEvent event){



    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onGenerateOre(GenerateMinable event){

        WorldGenerator gen = event.getGenerator();

    }

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void onBiomeEvent(BiomeEvent event){

        Biome b = event.getBiome();


    }

}
