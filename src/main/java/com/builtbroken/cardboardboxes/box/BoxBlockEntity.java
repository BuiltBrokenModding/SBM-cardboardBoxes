package com.builtbroken.cardboardboxes.box;

import java.util.Optional;

import com.builtbroken.cardboardboxes.Cardboardboxes;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * TileEntity for the box
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/28/2015.
 */
public class BoxBlockEntity extends BlockEntity {
    private BlockState placementState;
    private Optional<CompoundTag> placementData = Optional.empty();

    public BoxBlockEntity(BlockPos pos, BlockState state) {
        super(Cardboardboxes.BOX_BLOCK_ENTITY_TYPE.get(), pos, state);
    }

    @Override
    public void loadAdditional(ValueInput tag) {
        super.loadAdditional(tag);
        tag.getInt("storedTile").ifPresent(id -> {
            setStateForPlacement(Block.stateById(id));
            setDataForPlacement(tag.read("tileData", CompoundTag.CODEC));
        });
    }

    @Override
    public void saveAdditional(ValueOutput tag) {
        if (getStateForPlacement() != null) {
            tag.putInt("storedTile", Block.getId(placementState));
            getDataForPlacement().ifPresent(data -> {
                ValueOutput child = tag.child("tileData");
                child.store(data);
            });
        }
        super.saveAdditional(tag);
    }

    public BlockState getStateForPlacement() {
        return placementState;
    }

    public void setStateForPlacement(BlockState state) {
        this.placementState = state;
    }

    public Optional<CompoundTag> getDataForPlacement() {
        return placementData;
    }

    public void setDataForPlacement(Optional<CompoundTag> placementData) {
        this.placementData = placementData;
    }
}