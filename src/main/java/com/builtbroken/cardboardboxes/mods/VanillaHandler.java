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
import net.minecraftforge.common.ForgeConfigSpec;

public class VanillaHandler extends ModHandler
{
    private ForgeConfigSpec.BooleanValue spawnerVal;

    @Override
    public void build(ForgeConfigSpec.Builder b) {
        spawnerVal = b.comment("Prevents mobs spawners from being placed into cardboard boxes").define("BlackListSettings.BlackListMobSpawners", true);
    }

    @Override
    public void load(ForgeConfigSpec configuration)
    {
        if (spawnerVal.get())
        {
            HandlerManager.INSTANCE.banBlock(Blocks.SPAWNER);

            HandlerManager.INSTANCE.banTile(TileEntityType.MOB_SPAWNER);
        }

        //Fix for chests being rotated in opposite direction
        HandlerManager.INSTANCE.registerHandler(Blocks.CHEST, new Handler()
        {
            @Override
            public void postPlaceBlock(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ, IBlockState state, NBTTagCompound saveData)
            {
                IBlockState blockstate = worldIn.getBlockState(pos);
                if (blockstate.getBlock() == Blocks.CHEST && blockstate.get(BlockChest.FACING) != player.getHorizontalFacing().getOpposite())
                {
                    blockstate = blockstate.with(BlockChest.FACING, player.getHorizontalFacing().getOpposite());
                    worldIn.setBlockState(pos, blockstate);
                }
            }
        });

        //Remove unwanted interaction
        HandlerManager.INSTANCE.banBlock(Blocks.BEACON);
        HandlerManager.INSTANCE.banTile(TileEntityType.BEACON);
        HandlerManager.INSTANCE.banBlock(Blocks.PISTON);
        HandlerManager.INSTANCE.banBlock(Blocks.MOVING_PISTON);
        HandlerManager.INSTANCE.banBlock(Blocks.PISTON_HEAD);
        HandlerManager.INSTANCE.banBlock(Blocks.STICKY_PISTON);
        HandlerManager.INSTANCE.banTile(TileEntityType.PISTON);
        HandlerManager.INSTANCE.banBlock(Blocks.DAYLIGHT_DETECTOR);
        HandlerManager.INSTANCE.banTile(TileEntityType.DAYLIGHT_DETECTOR);
        HandlerManager.INSTANCE.banBlock(Blocks.ENDER_CHEST);
        HandlerManager.INSTANCE.banTile(TileEntityType.ENDER_CHEST);
        HandlerManager.INSTANCE.banBlock(Blocks.COMPARATOR);
        HandlerManager.INSTANCE.banTile(TileEntityType.COMPARATOR);
        HandlerManager.INSTANCE.banBlock(Blocks.COMMAND_BLOCK);
        HandlerManager.INSTANCE.banTile(TileEntityType.COMMAND_BLOCK);
        HandlerManager.INSTANCE.banBlock(Blocks.END_PORTAL);
        HandlerManager.INSTANCE.banBlock(Blocks.END_PORTAL_FRAME);
        HandlerManager.INSTANCE.banTile(TileEntityType.END_PORTAL);
        HandlerManager.INSTANCE.banBlock(Blocks.NOTE_BLOCK);
        HandlerManager.INSTANCE.banBlock(Blocks.ENCHANTING_TABLE);
        HandlerManager.INSTANCE.banTile(TileEntityType.ENCHANTING_TABLE);
        HandlerManager.INSTANCE.banBlock(Blocks.SIGN);
        HandlerManager.INSTANCE.banBlock(Blocks.WALL_SIGN);
        HandlerManager.INSTANCE.banTile(TileEntityType.SIGN);
        HandlerManager.INSTANCE.banBlock(Blocks.SKELETON_SKULL);
        HandlerManager.INSTANCE.banBlock(Blocks.SKELETON_WALL_SKULL);
        HandlerManager.INSTANCE.banBlock(Blocks.WITHER_SKELETON_SKULL);
        HandlerManager.INSTANCE.banBlock(Blocks.WITHER_SKELETON_WALL_SKULL);
        HandlerManager.INSTANCE.banBlock(Blocks.CREEPER_HEAD);
        HandlerManager.INSTANCE.banBlock(Blocks.CREEPER_WALL_HEAD);
        HandlerManager.INSTANCE.banBlock(Blocks.DRAGON_HEAD);
        HandlerManager.INSTANCE.banBlock(Blocks.DRAGON_WALL_HEAD);
        HandlerManager.INSTANCE.banBlock(Blocks.PLAYER_HEAD);
        HandlerManager.INSTANCE.banBlock(Blocks.PLAYER_WALL_HEAD);
        HandlerManager.INSTANCE.banBlock(Blocks.ZOMBIE_HEAD);
        HandlerManager.INSTANCE.banBlock(Blocks.ZOMBIE_WALL_HEAD);
        HandlerManager.INSTANCE.banTile(TileEntityType.SKULL);
        HandlerManager.INSTANCE.banBlock(Blocks.CAULDRON);
        HandlerManager.INSTANCE.banBlock(Blocks.FLOWER_POT);

        //Black listed because (A can already be moved, B duplicaiton issue
        HandlerManager.INSTANCE.banBlock(Blocks.SHULKER_BOX);
        HandlerManager.INSTANCE.banBlock(Blocks.WHITE_SHULKER_BOX);
        HandlerManager.INSTANCE.banBlock(Blocks.ORANGE_SHULKER_BOX);
        HandlerManager.INSTANCE.banBlock(Blocks.MAGENTA_SHULKER_BOX);
        HandlerManager.INSTANCE.banBlock(Blocks.LIGHT_BLUE_SHULKER_BOX);
        HandlerManager.INSTANCE.banBlock(Blocks.YELLOW_SHULKER_BOX);
        HandlerManager.INSTANCE.banBlock(Blocks.LIME_SHULKER_BOX);
        HandlerManager.INSTANCE.banBlock(Blocks.LIGHT_GRAY_SHULKER_BOX);
        HandlerManager.INSTANCE.banBlock(Blocks.PINK_SHULKER_BOX);
        HandlerManager.INSTANCE.banBlock(Blocks.GRAY_SHULKER_BOX);
        HandlerManager.INSTANCE.banBlock(Blocks.CYAN_SHULKER_BOX);
        HandlerManager.INSTANCE.banBlock(Blocks.PURPLE_SHULKER_BOX);
        HandlerManager.INSTANCE.banBlock(Blocks.BLUE_SHULKER_BOX);
        HandlerManager.INSTANCE.banBlock(Blocks.BROWN_SHULKER_BOX);
        HandlerManager.INSTANCE.banBlock(Blocks.GREEN_SHULKER_BOX);
        HandlerManager.INSTANCE.banBlock(Blocks.RED_SHULKER_BOX);
        HandlerManager.INSTANCE.banBlock(Blocks.BLACK_SHULKER_BOX);
        HandlerManager.INSTANCE.banTile(TileEntityType.SHULKER_BOX);
    }
}
