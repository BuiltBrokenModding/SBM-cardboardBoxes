package com.builtbroken.cardboardboxes.mods;

import com.builtbroken.cardboardboxes.handler.Handler;
import com.builtbroken.cardboardboxes.handler.HandlerManager;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ModConfigSpec;

public class VanillaHandler extends ModHandler {
    private ModConfigSpec.BooleanValue spawnerVal;

    @Override
    public void build(ModConfigSpec.Builder b) {
        spawnerVal = b.comment("Prevents mobs spawners from being placed into cardboard boxes").define("BlackListSettings.BlackListMobSpawners", true);
    }

    @Override
    public void load(ModConfigSpec configuration) {
        if (spawnerVal.get()) {
            HandlerManager.INSTANCE.banBlock(Blocks.SPAWNER);
            HandlerManager.INSTANCE.banBlockEntity(BlockEntityTypes.MOB_SPAWNER);
            HandlerManager.INSTANCE.banBlock(Blocks.TRIAL_SPAWNER);
            HandlerManager.INSTANCE.banBlockEntity(BlockEntityTypes.TRIAL_SPAWNER);
        }

        //Fix for chests being rotated in opposite direction
        HandlerManager.INSTANCE.registerHandler(Blocks.CHEST, new Handler() {
            @Override
            public void postPlaceBlock(Player player, Level level, BlockPos pos, InteractionHand hand, Direction direction, float hitX, float hitY, float hitZ, BlockState state, CompoundTag saveData) {
                BlockState blockstate = level.getBlockState(pos);
                if (blockstate.getBlock() == Blocks.CHEST && blockstate.getValue(ChestBlock.FACING) != player.getDirection().getOpposite()) {
                    blockstate = blockstate.setValue(ChestBlock.FACING, player.getDirection().getOpposite());
                    level.setBlockAndUpdate(pos, blockstate);
                }
            }
        });

        //Remove unwanted interaction
        HandlerManager.INSTANCE.banBlock(Blocks.BEACON);
        HandlerManager.INSTANCE.banBlockEntity(BlockEntityTypes.BEACON);
        HandlerManager.INSTANCE.banBlock(Blocks.PISTON);
        HandlerManager.INSTANCE.banBlock(Blocks.MOVING_PISTON);
        HandlerManager.INSTANCE.banBlock(Blocks.PISTON_HEAD);
        HandlerManager.INSTANCE.banBlock(Blocks.STICKY_PISTON);
        HandlerManager.INSTANCE.banBlockEntity(BlockEntityTypes.PISTON);
        HandlerManager.INSTANCE.banBlock(Blocks.DAYLIGHT_DETECTOR);
        HandlerManager.INSTANCE.banBlockEntity(BlockEntityTypes.DAYLIGHT_DETECTOR);
        HandlerManager.INSTANCE.banBlock(Blocks.ENDER_CHEST);
        HandlerManager.INSTANCE.banBlockEntity(BlockEntityTypes.ENDER_CHEST);
        HandlerManager.INSTANCE.banBlock(Blocks.COMPARATOR);
        HandlerManager.INSTANCE.banBlockEntity(BlockEntityTypes.COMPARATOR);
        HandlerManager.INSTANCE.banBlock(Blocks.COMMAND_BLOCK);
        HandlerManager.INSTANCE.banBlockEntity(BlockEntityTypes.COMMAND_BLOCK);
        HandlerManager.INSTANCE.banBlock(Blocks.END_PORTAL);
        HandlerManager.INSTANCE.banBlock(Blocks.END_PORTAL_FRAME);
        HandlerManager.INSTANCE.banBlockEntity(BlockEntityTypes.END_PORTAL);
        HandlerManager.INSTANCE.banBlock(Blocks.NOTE_BLOCK);
        HandlerManager.INSTANCE.banBlock(Blocks.ENCHANTING_TABLE);
        HandlerManager.INSTANCE.banBlockEntity(BlockEntityTypes.ENCHANTING_TABLE);
        HandlerManager.INSTANCE.banBlockEntity(BlockEntityTypes.SIGN);
        HandlerManager.INSTANCE.banBlockEntity(BlockEntityTypes.SIGN);
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
        HandlerManager.INSTANCE.banBlockEntity(BlockEntityTypes.SKULL);
        HandlerManager.INSTANCE.banBlock(Blocks.CAULDRON);
        HandlerManager.INSTANCE.banBlock(Blocks.FLOWER_POT);

        //Black listed because A) can already be moved, B) duplication issue
        HandlerManager.INSTANCE.banBlock(Blocks.SHULKER_BOX);
        Blocks.DYED_SHULKER_BOX.forEach(HandlerManager.INSTANCE::banBlock);
        HandlerManager.INSTANCE.banBlockEntity(BlockEntityTypes.SHULKER_BOX);
    }
}
