package com.builtbroken.cardboardboxes.handler;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Handles interaction between the box and a single tile. Allows for customizing save/load and placement.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/28/2015.
 */
public class Handler {
    /**
     * Called to handle special saving and loading for a
     * tile. Including stripping NBT data that shouldn't
     * exist on a replaced tile. Such as position data,
     * excluding tile's XYZ which is already removed.
     *
     * @param tag - save data, tile.writeToNBT is
     *            already called
     * @return save data, never should return null
     */
    public CompoundNBT save(CompoundNBT tag) {
        return tag;
    }

    /**
     * Called to load data into the tile
     */
    public void loadData(BlockState state, TileEntity tile, CompoundNBT tag) {
        tile.load(state, tag);
    }

    /**
     * Called to place the tile
     *
     * @param saveData - data for the tile entity
     * @return true if placement was handled, false to let default code run
     */
    public boolean placeBlock(PlayerEntity player, World worldIn, BlockPos pos, Hand hand, Direction direction, float hitX, float hitY, float hitZ, BlockState state, CompoundNBT saveData) {
        return false;
    }

    /**
     * Called after the block has been placed to do post interaction
     *
     * @param saveData - data for the tile entity
     */
    public void postPlaceBlock(PlayerEntity player, World worldIn, BlockPos pos, Hand hand, Direction direction, float hitX, float hitY, float hitZ, BlockState state, CompoundNBT saveData) {

    }
}