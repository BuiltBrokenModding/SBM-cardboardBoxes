package com.builtbroken.cardboardboxes.box;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

/**
 * Created by Dark on 7/28/2015.
 */
public class TileBox extends TileEntity
{
    ItemStack storedItem;
    NBTTagCompound tileData;

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        if (nbt.hasKey("storedTile"))
        {
            storedItem = new ItemStack(nbt.getCompoundTag("storedTile"));
            if (nbt.hasKey("tileData"))
            {
                tileData = nbt.getCompoundTag("tileData");
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        if (storedItem != null)
        {
            nbt.setTag("storedTile", storedItem.writeToNBT(new NBTTagCompound()));
            if (tileData != null)
            {
                nbt.setTag("tileData", tileData);
            }
        }
        return nbt;
    }
}