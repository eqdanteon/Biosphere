package net.gameovr.biosphere;

import net.gameovr.biosphere.biome.BiosphereBiomeProvider;
import net.gameovr.biosphere.config.ModConfig;
import net.minecraft.block.BlockGravel;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockSand.EnumType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraftforge.common.DimensionManager;

import java.util.ArrayList;
import java.util.Random;

import static net.minecraft.block.BlockStoneSlabNew.EnumType.RED_SANDSTONE;
import static net.minecraft.init.Blocks.SANDSTONE;

public class BiosphereWorldType extends WorldType {

    public static ArrayList<Sphere> spheres = new ArrayList<Sphere>();
    public static Random rand;
    public static BlockPos spawnPoint = null;

    public BiosphereWorldType(String name) {
        super(name);
    }

    @Override
    public BiomeProvider getBiomeProvider(World world) {

        return new BiosphereBiomeProvider(world.getWorldInfo());
    }

    @Override
    public IChunkGenerator getChunkGenerator(World world, String generatorOptions)
    {
        return new ChunkProviderBiosphere(world, world.getSeed());
    }

    @Override
    public void onGUICreateWorldPress() {


        DimensionManager.unregisterDimension(0);
        DimensionType bioOverworld = DimensionType.register("Overworld", "", 0, BiosphereWorldProvider.class, false );
        DimensionManager.registerDimension(0, bioOverworld);

        super.onGUICreateWorldPress();

    }

    @Override
    public int getMinimumSpawnHeight(World world) {
        // move spawn height up 1 each if world floor and
        // or world floor buffer is set
        // 16 is the min radius of a sphere
        int minY = ModConfig.worldFloor ? 1 : 0;
        minY += ModConfig.worldFloorBuffer ? 1 : 0;
        minY += 16;

        return minY;

    }

    public static void genSphereTerrainBlocks(Sphere sphere, Biome biome, Random rand, ChunkPrimer chunkPrimerIn, int x, int z, double noiseVal){

        IBlockState ICE = Blocks.ICE.getDefaultState();
        IBlockState WATER = Blocks.WATER.getDefaultState();
        IBlockState STONE = Blocks.STONE.getDefaultState();
        IBlockState AIR = Blocks.AIR.getDefaultState();
        /** The block expected to be on the top of this biome */
        IBlockState defaultTopBlock = Blocks.GRASS.getDefaultState();
        /** The block to fill spots in when not on the top */
        IBlockState defaultFillerBlock = Blocks.DIRT.getDefaultState();
        IBlockState topBlock = defaultTopBlock;
        IBlockState fillerBlock = defaultFillerBlock;
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

        int groundLevel = sphere.getSphereGroundLevel();
        int adjustedRadius = sphere.radius - (sphere.getOrigin().getY() - groundLevel);

        int noise = (int)(noiseVal / 3.0D + 3.0D + rand.nextDouble() * 0.25D);
        int replaceFlag = -1;
        int blockX = x & 15;
        int blockZ = z & 15;


        // starting at sphere ground level and going down until radius
        for (int blockY = groundLevel; blockY >  adjustedRadius; --blockY){

            IBlockState currentBlock = chunkPrimerIn.getBlockState(blockX, blockY, blockZ);

            if(currentBlock.getMaterial() == Material.AIR || currentBlock.getBlock() == Blocks.GLASS){
                replaceFlag = -1;
            }
            else if (currentBlock.getBlock() == Blocks.STONE){

                if(replaceFlag == -1){

                    if(noise <= 0){
                        topBlock = AIR;
                        fillerBlock = STONE;
                    }
                    else if(blockY >= groundLevel - 4 && blockY <= groundLevel + 1){
                        // 5 layers of grass and dirt
                        topBlock = defaultTopBlock;
                        fillerBlock = defaultFillerBlock;
                    }

                    if(blockY < groundLevel && (topBlock == null || topBlock.getMaterial() == Material.AIR))
                    {
                        if (biome.getFloatTemperature(blockpos$mutableblockpos.setPos(x, blockY, z)) < 0.15F)
                        {
                            topBlock = ICE;
                        }
                        else
                        {
                            topBlock = WATER;
                        }
                    }

                    replaceFlag = noise;

                    if (biome == Biomes.OCEAN || biome == Biomes.DEEP_OCEAN){
                        topBlock = WATER;
                        fillerBlock = WATER;
                        groundLevel = biome == Biomes.DEEP_OCEAN ? sphere.getOrigin().getY() + sphere.getRadius() : groundLevel;
                        chunkPrimerIn.setBlockState(blockX, blockY, blockZ, topBlock);
                        chunkPrimerIn.setBlockState(blockX, blockY, blockZ, fillerBlock);
                    }

                    else if(blockY >= groundLevel -1){
                        chunkPrimerIn.setBlockState(blockX, blockY, blockZ, topBlock);
                    }
                    else if(blockY < groundLevel - 7 - noise){
                        topBlock = AIR;
                        fillerBlock = STONE;
                        chunkPrimerIn.setBlockState(blockX, blockY, blockZ, Blocks.GRAVEL.getDefaultState());
                    }
                    else{
                        chunkPrimerIn.setBlockState(blockX, blockY, blockZ, fillerBlock);
                    }


                }

            }

            else if(replaceFlag > 0){

                --replaceFlag;
                chunkPrimerIn.setBlockState(blockX, blockY, blockZ, fillerBlock);

                if(replaceFlag == 0 && fillerBlock.getBlock() == Blocks.SAND && noise > 1){
                    replaceFlag = rand.nextInt(4) + Math.max(0, blockY - sphere.getRadius());
                    fillerBlock = fillerBlock.getValue(BlockSand.VARIANT) == EnumType.RED_SAND ? Blocks.RED_SANDSTONE.getDefaultState() : Blocks.SANDSTONE.getDefaultState();
                }

            }

        }






    }

}