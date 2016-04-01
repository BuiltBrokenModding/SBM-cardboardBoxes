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

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        if (nbt.hasKey("storedTile"))
        {
            storedItem = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("storedTile"));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        if (storedItem != null)
        {
            nbt.setTag("storedTile", storedItem.writeToNBT(new NBTTagCompound()));
        }
    }

    @Override
    public boolean canUpdate()
    {
        return false;
    }
}
