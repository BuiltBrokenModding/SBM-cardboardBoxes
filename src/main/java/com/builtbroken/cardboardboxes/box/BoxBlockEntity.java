package com.builtbroken.cardboardboxes.box;

import com.builtbroken.cardboardboxes.Cardboardboxes;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * TileEntity for the box
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/28/2015.
 */
public class BoxBlockEntity extends BlockEntity {
    private BlockState placementState;
    private CompoundTag placementData;

    public BoxBlockEntity(BlockPos pos, BlockState state) {
        super(Cardboardboxes.BOX_BLOCK_ENTITY_TYPE.get(), pos, state);
    }


    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("storedTile")) {
            setStateForPlacement(Block.stateById(tag.getInt("storedTile")));
            if (tag.contains("tileData")) {
                setDataForPlacement(tag.getCompound("tileData"));
            }
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        if (getStateForPlacement() != null) {
            tag.putInt("storedTile", Block.getId(placementState));
            if (getDataForPlacement() != null) {
                tag.put("tileData", getDataForPlacement());
            }
        }
    }

    public BlockState getStateForPlacement() {
        return placementState;
    }

    public void setStateForPlacement(BlockState state) {
        this.placementState = state;
    }

    public CompoundTag getDataForPlacement() {
        return placementData;
    }

    public void setDataForPlacement(CompoundTag placementData) {
        this.placementData = placementData;
    }
}