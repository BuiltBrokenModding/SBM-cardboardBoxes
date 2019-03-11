package com.builtbroken.cardboardboxes.box;

import com.builtbroken.cardboardboxes.Cardboardboxes;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

/**
 * TileEntity for the box
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/28/2015.
 */
public class TileEntityBox extends TileEntity
{
    private IBlockState placementState;
    private NBTTagCompound placementData;

    public TileEntityBox() {
        super(Cardboardboxes.tileBox);
    }

    @Override
    public void read(NBTTagCompound nbt)
    {
        super.read(nbt);
        if (nbt.hasKey("storedTile"))
        {
            setStateForPlacement(Block.getStateById(nbt.getInt("storedTile")));
            if (nbt.hasKey("tileData"))
            {
                setDataForPlacement(nbt.getCompound("tileData"));
            }
        }
    }

    @Override
    public NBTTagCompound write(NBTTagCompound nbt)
    {
        super.write(nbt);
        if (getStateForPlacement() != null)
        {
            nbt.setInt("storedTile", Block.getStateId(placementState));
            if (getDataForPlacement() != null)
            {
                nbt.setTag("tileData", getDataForPlacement());
            }
        }
        return nbt;
    }

    public IBlockState getStateForPlacement()
    {
        return placementState;
    }

    public void setStateForPlacement(IBlockState state)
    {
        this.placementState = state;
    }

    public NBTTagCompound getDataForPlacement()
    {
        return placementData;
    }

    public void setDataForPlacement(NBTTagCompound tileData)
    {
        this.placementData = tileData;
    }
}