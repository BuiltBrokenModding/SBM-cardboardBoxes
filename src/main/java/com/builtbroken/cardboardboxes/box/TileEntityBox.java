package com.builtbroken.cardboardboxes.box;

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
            if (getDataForPlacement() != null)
            {
                nbt.setTag("tileData", getDataForPlacement());
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