package com.builtbroken.cardboardboxes.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.builtbroken.cardboardboxes.box.BoxItemBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

/**
 * Handles interaction between {@link BoxItemBlock} and tiles
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
     * Map of tiles to handlers that provide special interaction
     */
    public static HashMap<Class<? extends BlockEntity>, Handler> pickupHandlerMap = new HashMap<>();
    /**
     * Map of block to handlers that provide special interaction
     */
    public static HashMap<Block, Handler> handlerMap = new HashMap<>();
    /**
     * List of tiles that are banned
     */
    public static List<BlockEntityType<?>> tileEntityBanList = new ArrayList<>();
    /**
     * List of Blocks that are banned
     */
    public static List<Block> blockBanList = new ArrayList<>();

    /**
     * Called to register a handler for managing the pickup state of a tile
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
     * Called to ban a tile
     */
    public void banTile(BlockEntityType<?> tile) {
        if (!tileEntityBanList.contains(tile)) {
            tileEntityBanList.add(tile);
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
            BlockEntity tile = world.getBlockEntity(pos);
            if (tile != null) {
                if (!tileEntityBanList.contains(tile.getType())) {
                    //Check if we even have data to store, no data no point in using a box
                    CompoundTag nbt = new CompoundTag();
                    tile.save(nbt);
                    nbt.remove("x");
                    nbt.remove("y");
                    nbt.remove("z");
                    nbt.remove("id");
                    return !nbt.isEmpty() ? CanPickUpResult.CAN_PICK_UP : CanPickUpResult.NO_DATA;
                }
                return CanPickUpResult.BANNED_TILE;
            }
            return CanPickUpResult.NO_TILE;
        }
        return CanPickUpResult.BANNED_BLOCK;
    }

}