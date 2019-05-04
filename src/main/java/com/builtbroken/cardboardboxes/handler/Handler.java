package com.builtbroken.cardboardboxes.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Handles interaction between the box and a single tile. Allows for customizing save/load and placement.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/28/2015.
 */
public class Handler
{
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
    public NBTTagCompound save(NBTTagCompound tag)
    {
        return tag;
    }

    /**
     * Called to load data into the tile
     *
     * @param tile
     * @param tag
     */
    public void loadData(TileEntity tile, NBTTagCompound tag)
    {
        tile.readFromNBT(tag);
    }

    /**
     * Called to place the tile
     *
     * @param stack    - block as a stack
     * @param saveData - data for the tile entity
     * @return true if placement was handled, false to let default code run
     */
    public boolean placeBlock(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ, ItemStack stack, NBTTagCompound saveData)
    {
        return false;
    }

    /**
     * Called after the block has been placed to do post interaction.
     * <p>
     * This is called before the tile entity has loaded use {@link #postPlaceTileEntity(EntityPlayer, TileEntity, EnumHand, EnumFacing, float, float, float, ItemStack, NBTTagCompound)}
     * for full data access.
     *
     * @param stack    - block as a stack
     * @param saveData - data for the tile entity
     */
    public void postPlaceBlock(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ, ItemStack stack, NBTTagCompound saveData)
    {

    }

    /**
     * Called after the block and tileEntity has been placed to do post interaction
     *
     * @param stack    - block as a stack
     * @param saveData - data for the tile entity
     */
    public void postPlaceTileEntity(EntityPlayer player, TileEntity entity, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ, ItemStack stack, NBTTagCompound saveData)
    {

    }
}