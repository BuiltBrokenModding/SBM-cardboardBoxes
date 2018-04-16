package com.builtbroken.cardboardboxes.mods;

import com.builtbroken.cardboardboxes.handler.Handler;
import com.builtbroken.cardboardboxes.handler.HandlerManager;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.*;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

public class VanillaHandler extends ModHandler
{
    @Override
    public void load(Configuration configuration)
    {
        if (configuration.getBoolean("BlackListMobSpawners", "BlackListSettings", true, "Prevents mobs spawners from being placed into cardboard boxes"))
        {
            HandlerManager.INSTANCE.banBlock(Blocks.MOB_SPAWNER);
            HandlerManager.INSTANCE.banTile(TileEntityMobSpawner.class);
        }

        //Fix for chests being rotated in opposite direction
        HandlerManager.INSTANCE.registerHandler(Blocks.CHEST, new Handler()
        {
            @Override
            public void postPlaceBlock(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ, ItemStack stack, NBTTagCompound saveData)
            {
                IBlockState blockstate = worldIn.getBlockState(pos);
                if (blockstate.getBlock() == Blocks.CHEST && blockstate.getValue(BlockChest.FACING) != player.getHorizontalFacing().getOpposite())
                {
                    blockstate = blockstate.withProperty(BlockChest.FACING, player.getHorizontalFacing().getOpposite());
                    worldIn.setBlockState(pos, blockstate);
                }
            }
        });

        //Remove unwanted interaction
        HandlerManager.INSTANCE.banBlock(Blocks.BEACON);
        HandlerManager.INSTANCE.banTile(TileEntityBeacon.class);
        HandlerManager.INSTANCE.banBlock(Blocks.PISTON);
        HandlerManager.INSTANCE.banBlock(Blocks.PISTON_EXTENSION);
        HandlerManager.INSTANCE.banBlock(Blocks.PISTON_HEAD);
        HandlerManager.INSTANCE.banBlock(Blocks.STICKY_PISTON);
        HandlerManager.INSTANCE.banTile(TileEntityPiston.class);
        HandlerManager.INSTANCE.banBlock(Blocks.DAYLIGHT_DETECTOR);
        HandlerManager.INSTANCE.banTile(TileEntityDaylightDetector.class);
        HandlerManager.INSTANCE.banBlock(Blocks.ENDER_CHEST);
        HandlerManager.INSTANCE.banTile(TileEntityEnderChest.class);
        HandlerManager.INSTANCE.banBlock(Blocks.POWERED_COMPARATOR);
        HandlerManager.INSTANCE.banBlock(Blocks.UNPOWERED_COMPARATOR);
        HandlerManager.INSTANCE.banTile(TileEntityComparator.class);
        HandlerManager.INSTANCE.banBlock(Blocks.COMMAND_BLOCK);
        HandlerManager.INSTANCE.banTile(TileEntityCommandBlock.class);
        HandlerManager.INSTANCE.banBlock(Blocks.END_PORTAL);
        HandlerManager.INSTANCE.banBlock(Blocks.END_PORTAL_FRAME);
        HandlerManager.INSTANCE.banTile(TileEntityEndPortal.class);
        HandlerManager.INSTANCE.banBlock(Blocks.NOTEBLOCK);
        HandlerManager.INSTANCE.banTile(TileEntityNote.class);
        HandlerManager.INSTANCE.banBlock(Blocks.ENCHANTING_TABLE);
        HandlerManager.INSTANCE.banTile(TileEntityEnchantmentTable.class);
        HandlerManager.INSTANCE.banBlock(Blocks.STANDING_SIGN);
        HandlerManager.INSTANCE.banBlock(Blocks.WALL_SIGN);
        HandlerManager.INSTANCE.banTile(TileEntitySign.class);
        HandlerManager.INSTANCE.banBlock(Blocks.SKULL);
        HandlerManager.INSTANCE.banTile(TileEntitySkull.class);
        HandlerManager.INSTANCE.banBlock(Blocks.CAULDRON);
        HandlerManager.INSTANCE.banBlock(Blocks.FLOWER_POT);
        HandlerManager.INSTANCE.banTile(TileEntityFlowerPot.class);
    }
}
