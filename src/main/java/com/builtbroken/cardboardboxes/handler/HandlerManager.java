package com.builtbroken.cardboardboxes.handler;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Dark on 7/28/2015.
 */
public class HandlerManager
{
    public static HashMap<Class<? extends TileEntity>, Handler> pickupHandlerMap = new HashMap();
    public static List<Class<? extends TileEntity>> blackListedTiles = new ArrayList();
    public static List<Block> blackListedBlocks = new ArrayList();


    public void registerSpecialHandler(Class<? extends TileEntity> clazz, Handler handler)
    {
        pickupHandlerMap.put(clazz, handler);
    }

    public void banTile(Class<? extends TileEntity> clazz)
    {
        if (!blackListedTiles.contains(clazz))
        {
            blackListedTiles.add(clazz);
        }
    }

    public void banBlock(Block block)
    {
        if (!blackListedBlocks.contains(block))
        {
            this.blackListedBlocks.add(block);
        }
    }

    public CanPickUpResult canPickUp(World world, int x, int y, int z)
    {
        Block block = world.getBlock(x, y, z);
        if (!blackListedBlocks.contains(block))
        {
            TileEntity tile = world.getTileEntity(x, y, z);
            if (tile != null)
            {
                if (!blackListedTiles.contains(tile.getClass()))
                {
                    //Check if we even have data to store, no data no point in using a box
                    NBTTagCompound nbt = new NBTTagCompound();
                    tile.writeToNBT(nbt);
                    nbt.removeTag("x");
                    nbt.removeTag("y");
                    nbt.removeTag("z");
                    nbt.removeTag("id");
                    return !nbt.hasNoTags() ? CanPickUpResult.CAN_PICK_UP : CanPickUpResult.NO_DATA;
                }
                return CanPickUpResult.BANNED_TILE;
            }
            return CanPickUpResult.NO_TILE;
        }
        return CanPickUpResult.BANNED_BLOCK;
    }

    public enum CanPickUpResult
    {
        CAN_PICK_UP,
        BANNED_BLOCK,
        BANNED_TILE,
        NO_TILE,
        NO_DATA
    }
}
