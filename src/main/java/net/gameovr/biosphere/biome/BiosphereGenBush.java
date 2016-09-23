package net.gameovr.biosphere.biome;

import net.minecraft.block.BlockBush;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenBush;

import java.util.Random;

public class BiosphereGenBush extends WorldGenBush {

    private BlockBush block;

    public BiosphereGenBush(BlockBush blockIn) {
        super(blockIn);
        block = blockIn;
    }

    @Override
    public boolean generate(World worldIn, Random rand, BlockPos position) {

        for (int i = 0; i < 64; ++i)
        {
            BlockPos blockpos = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));

            if (worldIn.isAirBlock(blockpos) && this.block.canBlockStay(worldIn, blockpos, this.block.getDefaultState()))
            {
                worldIn.setBlockState(blockpos, this.block.getDefaultState(), 2);
            }
        }

        return true;
    }
}
