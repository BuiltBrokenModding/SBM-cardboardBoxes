package com.builtbroken.cardboardboxes.box;

import com.builtbroken.cardboardboxes.Cardboardboxes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;

/**
 * TileEntity for the box
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/28/2015.
 */
public class TileEntityBox extends TileEntity {
    private BlockState placementState;
    private CompoundNBT placementData;

    public TileEntityBox() {
        super(Cardboardboxes.tileBox);
    }


    @Override
    public void load(BlockState state, CompoundNBT compoundNBT) {
        super.load(state, compoundNBT);
        if (compoundNBT.contains("storedTile")) {
            setStateForPlacement(Block.stateById(compoundNBT.getInt("storedTile")));
            if (compoundNBT.contains("tileData")) {
                setDataForPlacement(compoundNBT.getCompound("tileData"));
            }
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT compoundNBT) {
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

    public CompoundNBT getDataForPlacement() {
        return placementData;
    }

    public void setDataForPlacement(CompoundNBT tileData) {
        this.placementData = tileData;
    }
}