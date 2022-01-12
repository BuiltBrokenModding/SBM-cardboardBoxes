package com.builtbroken.cardboardboxes.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.builtbroken.cardboardboxes.box.BoxBlockItem;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

/**
 * Handles interaction between {@link BoxBlockItem} and tiles
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/28/2015.
 */
public class HandlerManager {
    /**
     * Primary manager
     */
    public final static HandlerManager INSTANCE = new HandlerManager();
    /**
     * Map of block entities to handlers that provide special interaction
     */
    public static HashMap<Class<? extends BlockEntity>, Handler> pickupHandlerMap = new HashMap<>();
    /**
     * Map of block to handlers that provide special interaction
     */
    public static HashMap<Block, Handler> handlerMap = new HashMap<>();
    /**
     * List of block entities that are banned
     */
    public static List<BlockEntityType<?>> blockEntityBanList = new ArrayList<>();
    /**
     * List of Blocks that are banned
     */
    public static List<Block> blockBanList = new ArrayList<>();

    /**
     * Called to register a handler for managing the pickup state of a block entity
     */
    public void registerPickupHandler(Class<? extends BlockEntity> clazz, Handler handler) //TODO implement
    {
        pickupHandlerMap.put(clazz, handler);
    }

    /**
     * Called to register an interaction handler to manage the overall state of a block
     *
     * @param block   - block to handle
     * @param handler - object to manage handler calls
     */
    public void registerHandler(Block block, Handler handler) {
        handlerMap.put(block, handler);
    }

    public Handler getHandler(Block block) {
        return handlerMap.get(block);
    }

    /**
     * Called to ban a block entity
     */
    public void banBlockEntity(BlockEntityType<?> tile) {
        if (!blockEntityBanList.contains(tile)) {
            blockEntityBanList.add(tile);
        }
    }

    /**
     * Called to ban a block
     */
    public void banBlock(Block block) {
        if (!blockBanList.contains(block)) {
            HandlerManager.blockBanList.add(block);
        }
    }

    /**
     * Called to check if a block can be picked up inside a box
     */
    public CanPickUpResult canPickUp(Level world, BlockPos pos) {
        Block block = world.getBlockState(pos).getBlock();
        if (!blockBanList.contains(block)) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity != null) {
                if (!blockEntityBanList.contains(blockEntity.getType())) {
                    //Check if we even have data to store, no data no point in using a box
                    return !blockEntity.saveWithoutMetadata().isEmpty() ? CanPickUpResult.CAN_PICK_UP : CanPickUpResult.NO_DATA;
                }
                return CanPickUpResult.BANNED_BLOCK_ENTITY;
            }
            return CanPickUpResult.NO_BLOCK_ENTITY;
        }
        return CanPickUpResult.BANNED_BLOCK;
    }

}