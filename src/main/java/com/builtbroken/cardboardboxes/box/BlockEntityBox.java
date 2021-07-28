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
public class BlockEntityBox extends BlockEntity {
    private BlockState placementState;
    private CompoundTag placementData;

    public BlockEntityBox(BlockPos pos, BlockState state) {
        super(Cardboardboxes.tileBox, pos, state);
    }


    @Override
    public void load(CompoundTag compoundNBT) {
        super.load(compoundNBT);
        if (compoundNBT.contains("storedTile")) {
            setStateForPlacement(Block.stateById(compoundNBT.getInt("storedTile")));
            if (compoundNBT.contains("tileData")) {
                setDataForPlacement(compoundNBT.getCompound("tileData"));
            }
        }
    }

    @Override
    public CompoundTag save(CompoundTag compoundNBT) {
        super.save(compoundNBT);
        if (getStateForPlacement() != null) {
            compoundNBT.putInt("storedTile", Block.getId(placementState));
            if (getDataForPlacement() != null) {
                compoundNBT.put("tileData", getDataForPlacement());
            }
        }
        return compoundNBT;
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

    public void setDataForPlacement(CompoundTag tileData) {
        this.placementData = tileData;
    }
}