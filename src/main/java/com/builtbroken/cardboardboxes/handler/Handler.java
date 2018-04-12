package com.builtbroken.cardboardboxes.handler;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

/**
 * Created by Dark on 7/28/2015.
 */
public class Handler {
    /**
     * Called to handle special saving and loading for a
     * tile. Including stripping NBT data that shouldn't
     * exist on a replaced tile. Such as position data,
     * excluding tile's XYZ which is already removed.
     *
     * @param tag - save data, tile.writeToNBT is
     *            already called
     * @return save data, never should return null
     */
    public NBTTagCompound save(NBTTagCompound tag) {
        return tag;
    }

    /**
     * Called to load data into the tile
     *
     * @param tile
     * @param tag
     */
    public void loadData(TileEntity tile, NBTTagCompound tag) {

    }

    /**
     * Called to place the tile
     *
     * @param stack
     * @param saveData
     */
    public void placeBlock(ItemStack stack, NBTTagCompound saveData) {

    }
}