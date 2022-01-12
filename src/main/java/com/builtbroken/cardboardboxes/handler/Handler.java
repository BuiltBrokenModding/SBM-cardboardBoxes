package com.builtbroken.cardboardboxes.handler;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Handles interaction between the box and a single tile. Allows for customizing save/load and placement.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/28/2015.
 */
public class Handler {
    /**
     * Called to handle special saving and loading for a
     * block entity. Including stripping NBT data that shouldn't
     * exist on a replaced block entity. Such as position data,
     * excluding block entity's XYZ which is already removed.
     *
     * @param tag - save data, the block entity's save routine
     *            is already called
     * @return save data, never should return null
     */
    public CompoundTag save(CompoundTag tag) {
        return tag;
    }

    /**
     * Called to load data into the tile
     */
    public void loadData(BlockEntity blockEntity, CompoundTag tag) {
        blockEntity.load(tag);
    }

    /**
     * Called to place the block entity
     *
     * @param saveData - data for the block entity
     * @return true if placement was handled, false to let default code run
     */
    public boolean placeBlock(Player player, Level level, BlockPos pos, InteractionHand hand, Direction direction, float hitX, float hitY, float hitZ, BlockState state, CompoundTag saveData) {
        return false;
    }

    /**
     * Called after the block has been placed to do post interaction
     *
     * @param saveData - data for the block entity
     */
    public void postPlaceBlock(Player player, Level level, BlockPos pos, InteractionHand hand, Direction direction, float hitX, float hitY, float hitZ, BlockState state, CompoundTag saveData) {

    }
}