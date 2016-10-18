package net.gameovr.biosphere;

import net.gameovr.biosphere.biome.BiosphereBiomeDecorator;
import net.gameovr.biosphere.config.ModConfig;
import net.gameovr.biosphere.helpers.BioLogger;
import net.gameovr.biosphere.helpers.ChunkCalculator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.WorldEntitySpawner;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.MapGenCaves;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;
import net.minecraftforge.event.terraingen.TerrainGen;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static net.minecraftforge.event.terraingen.InitMapGenEvent.EventType.CAVE;
import static net.minecraftforge.event.terraingen.InitMapGenEvent.EventType.SCATTERED_FEATURE;
import static net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.ANIMALS;
import static net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.LAKE;
import static net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.LAVA;

public class ChunkProviderBiosphere implements IChunkGenerator {

    private Random rand;
    private World world;
    private int WORLD_MAX_Y;
    private IBlockState bufferBlockState;
    private SphereManager spheremanager = new SphereManager();
    private Sphere nearestSphere = null;

    private MapGenBase caveGenerator;
    private MapGenScatteredFeature scatteredFeatureGenerator;

    private NoiseGeneratorPerlin stoneNoiseGen;
    private double[] stoneNoiseArray;
    private final double[] noiseArray;
    private NoiseGeneratorOctaves xyzNoiseGenA;
    private NoiseGeneratorOctaves xyzNoiseGenB;
    private NoiseGeneratorOctaves xyzBalanceNoiseGen;

    // settings
    private int lavaLakeChance = 100;
    private boolean useLavaLakes = true;

    public ChunkProviderBiosphere(World world, long seed) {
        this.world = world;
        rand = new Random(seed);
        WORLD_MAX_Y = world.getHeight();

        bufferBlockState = ModConfig.bufferBlock;

        //Generate list of spheres
        SphereManager sphereManager = new SphereManager();
        sphereManager.GenerateSpheres(seed);

        //setup terrain generators
        this.caveGenerator = TerrainGen.getModdedMapGen(new MapGenCaves(), CAVE);
        this.scatteredFeatureGenerator = (MapGenScatteredFeature) TerrainGen.getModdedMapGen(new MapGenScatteredFeature(), SCATTERED_FEATURE);

        // set up the noise generators
        this.xyzNoiseGenA = new NoiseGeneratorOctaves(this.rand, 16);
        this.xyzNoiseGenB = new NoiseGeneratorOctaves(this.rand, 16);
        this.xyzBalanceNoiseGen = new NoiseGeneratorOctaves(this.rand, 8);
        this.stoneNoiseGen = new NoiseGeneratorPerlin(this.rand, 4);
        this.stoneNoiseArray = new double[256];
        this.noiseArray = new double[825];

    }

    public Chunk provideChunk(int chunkX, int chunkZ) {

        ChunkPrimer chunkprimer = new ChunkPrimer();

        // start off by adding basic terrain: air, water, bedrock
        setWorldFloorOptions(chunkprimer, ModConfig.worldFloor, ModConfig.worldFloorBuffer);

        // hand over to the biomes for them to set bedrock grass and dirt
        Biome[] biomes = this.world.getBiomeProvider().getBiomesForGeneration(null, chunkX * 16, chunkZ * 16, 16, 16);

        // Add spheres to world
        drawSpheres(chunkX, chunkZ, chunkprimer);

        this.replaceBlocksForBiome(chunkX, chunkZ, chunkprimer, biomes);
        this.caveGenerator.generate(this.world, chunkX, chunkZ, chunkprimer);


        //Create and return chunk
        Chunk chunk = new Chunk(world, chunkprimer, chunkX, chunkZ);

        byte[] chunkBiomes = new byte[256];
        for (int i = 0; i < chunkBiomes.length; i++) {
            chunkBiomes[i] = (byte) Biome.getIdForBiome(biomes[i]);

        }
        chunk.setBiomeArray(chunkBiomes);



        chunk.generateSkylightMap();
        return chunk;
    }

    public void populate(int chunkX, int chunkZ) {

        populateFromSubChunkLoop(chunkX, chunkZ);

    }


    public boolean generateStructures(Chunk chunkIn, int chunkX, int chunkZ) {

       // Biosphere.logger.info("!!!!! Generate Structures was called !!!!!");

        return true;
    }


    public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
        return null;
    }

    @Nullable
    public BlockPos getStrongholdGen(World worldIn, String structureName, BlockPos position) {
        return null;
    }

    public void recreateStructures(Chunk chunkIn, int chunkX, int chunkZ) {
        //Biosphere.logger.info("!!!!! Recreate Structures was called !!!!!");

        // divide chunk into 16 subchunks in y direction
        for (int subChunkY = 0; subChunkY < 16; ++subChunkY) {

            BlockPos referenceBlock = ChunkCalculator.getSubChunkCenterPos(chunkX, subChunkY, chunkZ);
            nearestSphere = spheremanager.getNearestSphere(referenceBlock);



            // subchunk is 16 blocks high in y direction, index as jy
            for (int blockY = 0; blockY < 16; ++blockY) {

                // subchunk is 16 blocks long in x direction, index as jx
                for (int subChunkX = 0; subChunkX < 16; ++subChunkX) {

                    // subchunk is 16 blocks long in z direction, index as jz
                    for (int subChunkZ = 0; subChunkZ < 16; ++subChunkZ) {

                        int currentBlockX = (chunkX * 16) + subChunkX;
                        int currentBlockY = (subChunkY * 16) + blockY;
                        int currentBlockZ = (chunkZ * 16) + subChunkZ;
                        BlockPos currentBlockPos = new BlockPos(currentBlockX, currentBlockY, currentBlockZ);

                        // build bridge
                        if(nearestSphere.startBridgeConnection != null && nearestSphere.endBridgeConnection != null) {

                            if (currentBlockPos.equals(nearestSphere.startBridgeConnection) || currentBlockPos.equals(nearestSphere.endBridgeConnection)) {

                                chunkIn.setBlockState(currentBlockPos, Blocks.EMERALD_BLOCK.getDefaultState());
                            }

                            if(nearestSphere.bridgeBlocks.contains(currentBlockPos)){
                                chunkIn.setBlockState(currentBlockPos, Blocks.EMERALD_BLOCK.getDefaultState());
                            }



                        }
                    }
                }
            }
        }


    }

    public void setWorldFloorOptions(ChunkPrimer chunkprimer, boolean floor, boolean buffer) {
        for (int blockX = 0; blockX < 16; ++blockX) {
            for (int blockZ = 0; blockZ < 16; ++blockZ) {
                if (floor) {
                    chunkprimer.setBlockState(blockX, 0, blockZ, Blocks.BEDROCK.getDefaultState());
                }
                if (buffer) {
                    for (int blockY = 1; blockY < ModConfig.bufferThickness + 1; ++blockY) {
                        chunkprimer.setBlockState(blockX, blockY, blockZ, bufferBlockState);
                    }
                }
            }
        }
    }

    public void drawSpheres(int chunkX, int chunkZ, ChunkPrimer chunkPrimer) {

        drawSphereFromSuperLoop(chunkX, chunkZ, chunkPrimer);

    }

    private void drawSphereFromSuperLoop(int chunkX, int chunkZ, ChunkPrimer chunkPrimer) {



        // divide chunk into 16 subchunks in y direction, index as iy
        for (int subChunkY = 0; subChunkY < 16; ++subChunkY) {

            BlockPos referenceBlock = ChunkCalculator.getSubChunkCenterPos(chunkX, subChunkY, chunkZ);
            nearestSphere = spheremanager.getNearestSphere(referenceBlock);

            Vec3i lineSE = nearestSphere.startBridgeConnection.subtract(nearestSphere.endBridgeConnection);
            Vec3d line3dSE = new Vec3d(lineSE);
            Vec3i lineES = nearestSphere.endBridgeConnection.subtract(nearestSphere.startBridgeConnection);
            Vec3d line3dES = new Vec3d(lineES);

            // subchunk is 16 blocks high in y direction, index as jy
            for (int blockY = 0; blockY < 16; ++blockY) {

                // subchunk is 16 blocks long in x direction, index as jx
                for (int subChunkX = 0; subChunkX < 16; ++subChunkX) {

                    // subchunk is 16 blocks long in z direction, index as jz
                    for (int subChunkZ = 0; subChunkZ < 16; ++subChunkZ) {

                        int currentBlockX = (chunkX * 16) + subChunkX;
                        int currentBlockY = (subChunkY * 16) + blockY;
                        int currentBlockZ = (chunkZ * 16) + subChunkZ;
                        BlockPos currentBlockPos = new BlockPos(currentBlockX, currentBlockY, currentBlockZ);

                        if (nearestSphere.getDistanceFromOrigin(currentBlockX, currentBlockY, currentBlockZ) == nearestSphere.getRadius()) {

                            chunkPrimer.setBlockState(subChunkX, currentBlockY, subChunkZ, Blocks.GLASS.getDefaultState());
                        }
                        if (nearestSphere.getDistanceFromOrigin(currentBlockX, currentBlockY, currentBlockZ) < nearestSphere.getRadius() && currentBlockY < nearestSphere.getOrigin().getY() - 2) {

                            chunkPrimer.setBlockState(subChunkX, currentBlockY, subChunkZ, Blocks.STONE.getDefaultState());
                        }

                    }
                }
            }
        }
    }

    private void populateFromSubChunkLoop(int chunkX, int chunkZ){
        BlockFalling.fallInstantly = true;

        int x = chunkX * 16;
        int z = chunkZ * 16;

        // divide chunk into 16 subchunks in y direction
        for (int subChunkY = 0; subChunkY < 16; ++subChunkY) {


            BlockPos referenceBlock = ChunkCalculator.getSubChunkCenterPos(chunkX, subChunkY, chunkZ);

            nearestSphere = null;
            nearestSphere = spheremanager.getNearestSphere(referenceBlock);

            if (nearestSphere != null) {

                //BlockPos blockpos = referenceBlock;
                BlockPos blockpos = new BlockPos(x, nearestSphere.getOrigin().getY(), z);

                Biome Biome = this.world.getBiomeForCoordsBody(blockpos.add(8, 0, 8));
                BlockPos decorateStart = blockpos.add(8, 0, 8);

                boolean hasVillageGenerated = false;

                BlockPos target;

                // add water lakes
                if (!nearestSphere.getHasLake()) {
                    if (Biome.getRainfall() > 0.01F && Biome != Biomes.DESERT && Biome != Biomes.DESERT_HILLS && TerrainGen.populate(this, world, rand, chunkX, chunkZ, false, LAKE)) {

                        target = decorateStart.add(this.rand.nextInt(16), this.rand.nextInt(16), this.rand.nextInt(16));
                        if ((new WorldGenLakes(Blocks.WATER)).generate(this.world, this.rand, target)) {
                            nearestSphere.setHasLake();
                        }

                    }

                    // add lava lakes
                    if (TerrainGen.populate(this, world, rand, chunkX, chunkZ, hasVillageGenerated, LAVA) && !hasVillageGenerated && this.rand.nextInt(this.lavaLakeChance / 10) == 0 && this.useLavaLakes)
                    {
                        target = decorateStart.add(this.rand.nextInt(16), this.rand.nextInt(16), this.rand.nextInt(16));
                        if (this.rand.nextInt(this.lavaLakeChance / 8) == 0)
                        {
                            if ((new WorldGenLakes(Blocks.LAVA)).generate(this.world, this.rand, target)){
                                nearestSphere.setHasLake();
                            }

                        }
                    }

                }



                // hand over to the biome to decorate itself
                BiosphereBiomeDecorator bioDecorator = new BiosphereBiomeDecorator(nearestSphere);
                bioDecorator.decorate(this.world, this.rand, Biome, new BlockPos(x, nearestSphere.getOrigin().getY(), z));

                // add animals
                if (TerrainGen.populate(this, world, rand, chunkX, chunkZ, false, ANIMALS)) {
                    BiosphereWorldEntitySpawner.performWorldGenSpawning(this.world, Biome, x + 8, z + 8, 16, 16, this.rand, nearestSphere);
                }

            }
        }




        BlockFalling.fallInstantly = false;
    }

    public void replaceBlocksForBiome(int chunkX, int chunkZ, ChunkPrimer primer, Biome[] biomes) {
        // Biomes add their top blocks and filler blocks to the primer here
        if (!net.minecraftforge.event.ForgeEventFactory.onReplaceBiomeBlocks(this, chunkX, chunkZ, primer, this.world))
            return;

        double d0 = 0.03125D;
        this.stoneNoiseArray = this.stoneNoiseGen.getRegion(this.stoneNoiseArray, (double) (chunkX * 16), (double) (chunkZ * 16), 16, 16, d0 * 2.0D, d0 * 2.0D, 1.0D);

        for (int subChunkY = 0; subChunkY < 16; ++subChunkY) {

            BlockPos referenceBlock = ChunkCalculator.getSubChunkCenterPos(chunkX, subChunkY, chunkZ);

            nearestSphere = null;
            nearestSphere = spheremanager.getNearestSphere(referenceBlock);

            if(nearestSphere != null){

                for (int localX = 0; localX < 16; ++localX) {
                    for (int localZ = 0; localZ < 16; ++localZ) {

                        Biome biome = biomes[localZ + localX * 16];
                        BiosphereWorldType.genSphereTerrainBlocks(nearestSphere, biome, this.rand, primer, chunkX * 16 + localX, chunkZ * 16 + localZ, this.stoneNoiseArray[localZ + localX * 16]);


                    }
                }

            }

        }

    }


} //End Class
