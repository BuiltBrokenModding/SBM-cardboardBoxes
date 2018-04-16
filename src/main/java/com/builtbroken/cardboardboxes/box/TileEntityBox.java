package com.builtbroken.cardboardboxes.box;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

/**
 * Created by Dark on 7/28/2015.
 */
public class TileEntityBox extends TileEntity
{
    private ItemStack placementItem; //TODO convert to capability so we can recycle data for item
    private NBTTagCompound placementData;

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        if (nbt.hasKey("storedTile"))
        {
            setItemForPlacement(new ItemStack(nbt.getCompoundTag("storedTile")));
            if (nbt.hasKey("tileData"))
            {
                setDataForPlacement(nbt.getCompoundTag("tileData"));
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        if (getItemForPlacement() != null)
        {
            nbt.setTag("storedTile", getItemForPlacement().writeToNBT(new NBTTagCompound()));
            if (getTileData() != null)
            {
                nbt.setTag("tileData", getTileData());
            }
        }
        return nbt;
    }

    public ItemStack getItemForPlacement()
    {
        return placementItem;
    }

    public void setItemForPlacement(ItemStack storedItem)
    {
        this.placementItem = storedItem;
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