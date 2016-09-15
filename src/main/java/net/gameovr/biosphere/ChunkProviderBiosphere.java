package net.gameovr.biosphere;

import net.gameovr.biosphere.config.ModConfig;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.gen.NoiseGeneratorPerlin;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class ChunkProviderBiosphere implements IChunkGenerator {

    private Random rand;
    private World world;
    private int WORLD_MAX_Y;
    private IBlockState bufferBlockState;
    private SphereManager spheremanager = new SphereManager();

    private NoiseGeneratorPerlin stoneNoiseGen;
    private double[] stoneNoiseArray;

    public ChunkProviderBiosphere(World world, long seed) {
        this.world = world;
        rand = new Random(seed);
        WORLD_MAX_Y = world.getHeight();

        bufferBlockState = ModConfig.bufferBlock;

        //Generate list of spheres
        SphereManager sphereManager = new SphereManager();
        sphereManager.GenerateSpheres(seed);

        this.stoneNoiseGen = new NoiseGeneratorPerlin(this.rand, 4);
        this.stoneNoiseArray = new double[256];

    }

    public Chunk provideChunk(int chunkX, int chunkZ) {

        ChunkPrimer chunkprimer = new ChunkPrimer();

        // start off by adding basic terrain: air, water, bedrock
        setWorldFloorOptions(chunkprimer, ModConfig.worldFloor, ModConfig.worldFloorBuffer);


        // Add spheres to world
        drawSpheres(chunkX, chunkZ, chunkprimer);

        // hand over to the biomes for them to set bedrock grass and dirt
        Biome[] biomes = this.world.getBiomeProvider().getBiomesForGeneration(null, chunkX * 16, chunkZ * 16, 16, 16);
        this.replaceBlocksForBiome(chunkX, chunkZ, chunkprimer, biomes);


        Chunk chunk = new Chunk(world, chunkprimer, chunkX, chunkZ);

        byte[] chunkBiomes = chunk.getBiomeArray();
        for(int i = 0; i < chunkBiomes.length; ++i){

            chunkBiomes[i] = (byte)Biome.getIdForBiome(biomes[i]);
        }
        chunk.setBiomeArray(chunkBiomes);

        chunk.generateSkylightMap();
        return chunk;
    }

    public void populate(int chunkX, int chunkZ) {


    }

    public boolean generateStructures(Chunk chunkIn, int chunkX, int chunkZ) {
        return false;
    }

    public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
        return null;
    }

    @Nullable
    public BlockPos getStrongholdGen(World worldIn, String structureName, BlockPos position) {
        return null;
    }

    public void recreateStructures(Chunk chunkIn, int chunkX, int chunkZ) {

    }

    public void setWorldFloorOptions(ChunkPrimer chunkprimer, boolean floor, boolean buffer){
        for (int blockX = 0; blockX < 16; ++blockX) {
            for (int blockZ = 0; blockZ < 16; ++blockZ) {
                if(floor){chunkprimer.setBlockState(blockX,0,blockZ,Blocks.BEDROCK.getDefaultState());}
                if(buffer){
                    for (int blockY = 1; blockY < ModConfig.bufferThickness + 1; ++blockY) {
                        chunkprimer.setBlockState(blockX, blockY, blockZ, bufferBlockState);
                    }
                }
            }
        }
    }

    public void drawSpheres(int chunkX, int chunkZ, ChunkPrimer chunkPrimer) {


        // divide chunk into 16 subchunks in y direction, index as iy
        for (int iy = 0; iy < 16; ++iy) {

            BlockPos referenceBlock = new BlockPos((chunkX * 16) + 8, (iy * 16) + 8, (chunkZ * 16) + 8);
            Sphere nearestSphere = spheremanager.getNearestSphere(referenceBlock);

            // subchunk is 16 blocks high in y direction, index as jy
            for (int jy = 0; jy < 16; ++jy) {

                // subchunk is 16 blocks long in x direction, index as jx
                for (int jx = 0; jx < 16; ++jx) {

                    // subchunk is 16 blocks long in z direction, index as jz
                    for (int jz = 0; jz < 16; ++jz) {

                        int currentBlockX = (chunkX * 16) + jx;
                        int currentBlockY = (iy * 16) + jy;
                        int currentBlockZ = (chunkZ * 16) + jz;

                        //TODO: figure out Exception in server tick loop sometimes
                        if (nearestSphere.getDistanceFromOrigin(currentBlockX, currentBlockY, currentBlockZ) == nearestSphere.getRadius()) {

                            chunkPrimer.setBlockState(jx, currentBlockY, jz, Blocks.GLASS.getDefaultState());

                        }
                        if (nearestSphere.getDistanceFromOrigin(currentBlockX, currentBlockY, currentBlockZ) < nearestSphere.getRadius() && currentBlockY < nearestSphere.getOrigin().getY() - 2) {
                            chunkPrimer.setBlockState(jx, currentBlockY, jz, Blocks.GRASS.getDefaultState());
                        }
                        if (nearestSphere.getDistanceFromOrigin(currentBlockX, currentBlockY, currentBlockZ) < nearestSphere.getRadius() && currentBlockY < nearestSphere.getOrigin().getY() - 3) {
                            chunkPrimer.setBlockState(jx, currentBlockY, jz, Blocks.STONE.getDefaultState());
                        }
                    }
                }
            }
        }
    }

    // Biomes add their top blocks and filler blocks to the primer here
    public void replaceBlocksForBiome(int chunkX, int chunkZ, ChunkPrimer primer, Biome[] biomes)
    {
        if (!net.minecraftforge.event.ForgeEventFactory.onReplaceBiomeBlocks(this, chunkX, chunkZ, primer, this.world)) return;

        double d0 = 0.03125D;
        this.stoneNoiseArray = this.stoneNoiseGen.getRegion(this.stoneNoiseArray, (double)(chunkX * 16), (double)(chunkZ * 16), 16, 16, d0 * 2.0D, d0 * 2.0D, 1.0D);

        for (int localX = 0; localX < 16; ++localX)
        {
            for (int localZ = 0; localZ < 16; ++localZ)
            {
                Biome biome = biomes[localZ + localX * 16];
                biome.genTerrainBlocks(this.world, this.rand, primer, chunkX * 16 + localX, chunkZ * 16 + localZ, this.stoneNoiseArray[localZ + localX * 16]);
            }
        }
    }

} //End Class
