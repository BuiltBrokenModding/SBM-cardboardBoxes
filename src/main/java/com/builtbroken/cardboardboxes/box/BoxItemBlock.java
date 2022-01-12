package com.builtbroken.cardboardboxes.box;

import static com.builtbroken.cardboardboxes.box.BoxBlock.STORE_ITEM_TAG;
import static com.builtbroken.cardboardboxes.box.BoxBlock.TILE_DATA_TAG;

import java.util.List;

import com.builtbroken.cardboardboxes.Cardboardboxes;
import com.builtbroken.cardboardboxes.handler.CanPickUpResult;
import com.builtbroken.cardboardboxes.handler.Handler;
import com.builtbroken.cardboardboxes.handler.HandlerManager;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * ItemBlock for the box
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/28/2015.
 */
public class BoxItemBlock extends BlockItem {
    public BoxItemBlock(Block block) {
        super(block, new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS));
        this.setRegistryName(block.getRegistryName());
    }

    //TODO add property to change render if contains item
    //TODO add property to change render based on content (e.g. show chest on box)
    //TODO add property to change render color, label, etc

    @Override
    public InteractionResult useOn(UseOnContext context) {
        //Run all logic server side
        Level worldIn = context.getLevel();
        if (worldIn.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        Player player = context.getPlayer();
        InteractionHand hand = context.getHand();
        final ItemStack heldItemStack = context.getItemInHand();
        if (!heldItemStack.isEmpty()) {
            final BlockState storeBlock = getStoredBlock(heldItemStack);
            if (storeBlock.getBlock() != Blocks.AIR) {
                return tryToPlaceBlock(new BlockPlaceContext(context));
            } else {
                return tryToPickupBlock(player, worldIn, context.getClickedPos(), hand, context.getClickedFace());
            }
        }
        return InteractionResult.FAIL;
    }

    protected InteractionResult tryToPickupBlock(Player player, Level worldIn, BlockPos pos, InteractionHand hand, Direction direction) {
        //Check that we can pick up block
        CanPickUpResult result = HandlerManager.INSTANCE.canPickUp(worldIn, pos);

        if (result == CanPickUpResult.CAN_PICK_UP) {
            //Get tile, ignore anything without a tile
            BlockEntity tileEntity = worldIn.getBlockEntity(pos);
            if (tileEntity != null) {
                //Get stack
                final BlockState state = worldIn.getBlockState(pos);
                //Copy tile data
                CompoundTag nbtTagCompound = new CompoundTag();
                tileEntity.save(nbtTagCompound);

                //Remove location data
                nbtTagCompound.remove("x");
                nbtTagCompound.remove("y");
                nbtTagCompound.remove("z");

                //Remove tile
                worldIn.removeBlockEntity(pos);

                //Replace block with our block
                worldIn.setBlock(pos, Cardboardboxes.boxBlock.defaultBlockState(), 2);

                //Get our tile
                tileEntity = worldIn.getBlockEntity(pos);
                if (tileEntity instanceof BlockEntityBox tileBox) {
                    //Move data into tile
                    tileBox.setStateForPlacement(state);
                    tileBox.setDataForPlacement(nbtTagCompound);

                    //Consume item
                    player.getItemInHand(hand).shrink(1);

                    //Done
                    return InteractionResult.SUCCESS;
                }
            } else {
                player.displayClientMessage(new TranslatableComponent(getDescriptionId() + ".noData"), true);
            }
        } else if (result == CanPickUpResult.BANNED_TILE) {
            player.displayClientMessage(new TranslatableComponent(getDescriptionId() + ".banned.tile"), true);
        } else if (result == CanPickUpResult.BANNED_BLOCK) {
            player.displayClientMessage(new TranslatableComponent(getDescriptionId() + ".banned.block"), true);
        } else {
            player.displayClientMessage(new TranslatableComponent(getDescriptionId() + ".noData"), true);
        }
        return InteractionResult.SUCCESS;
    }

    protected InteractionResult tryToPlaceBlock(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        InteractionHand hand = context.getHand();
        //Move up one if not replaceable
        float hitX = (float) context.getClickLocation().x(), hitY = (float) context.getClickLocation().y(), hitZ = (float) context.getClickLocation().z();
        if (!context.canPlace()) {
            pos = pos.relative(context.getClickedFace());
        }

        final ItemStack heldItemStack = context.getItemInHand();
        final BlockState storedBlockState = getStoredBlock(heldItemStack);
        final CompoundTag savedTileData = getStoredTileData(heldItemStack);
        //Check if we can place the given block
        if (storedBlockState != null && context.getPlayer().mayUseItemAt(pos, context.getClickedFace(), heldItemStack) && context.getLevel().getBlockState(pos).getMaterial().isReplaceable()) {
            Handler handler = HandlerManager.INSTANCE.getHandler(storedBlockState.getBlock());
            BlockState blockstate = storedBlockState.getBlock().getStateForPlacement(context);
            //Allow handler to control placement
            if (handler != null && handler.placeBlock(context.getPlayer(), context.getLevel(), pos, hand, context.getClickedFace(), hitX, hitY, hitZ, storedBlockState, savedTileData)
                    //Run normal placement if we don't have a handler or it didn't do anything
                    || placeBlock(context, blockstate)) {
                //Get placed block
                blockstate = context.getLevel().getBlockState(pos);

                //Allow handle to do post placement modification (e.g. fix rotation)
                if (handler != null) {
                    handler.postPlaceBlock(context.getPlayer(), context.getLevel(), pos, hand, context.getClickedFace(), hitX, hitY, hitZ, storedBlockState, savedTileData);
                }

                //Set tile entity data
                if (savedTileData != null) {
                    BlockEntity blockEntity = context.getLevel().getBlockEntity(pos);
                    if (blockEntity != null) {
                        if (handler != null) {
                            handler.loadData(blockEntity, savedTileData);
                        } else {
                            blockEntity.load(savedTileData);
                        }
                    }
                }


                //Place audio
                SoundType soundtype = blockstate.getBlock().getSoundType(blockstate, context.getLevel(), pos, context.getPlayer());
                context.getLevel().playSound(null, pos, soundtype.getPlaceSound(), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);

                //Consume item
                heldItemStack.shrink(1);

                //Return empty box
                if (!context.getPlayer().isCreative() && !context.getPlayer().getInventory().add(new ItemStack(Cardboardboxes.boxBlock))) {
                    context.getPlayer().spawnAtLocation(new ItemStack(Cardboardboxes.boxBlock), 0F);
                }
            }

            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.FAIL;
        }
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        return stack.hasTag() ? 1 : 64;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        if (stack.getTag() != null && stack.getTag().contains(STORE_ITEM_TAG)) {
            BlockState state = Block.stateById(stack.getTag().getInt(STORE_ITEM_TAG));
            {
                tooltip.add(new TranslatableComponent(state.getBlock().getDescriptionId()));
            }
        }
    }

    public BlockState getStoredBlock(ItemStack stack) {
        return stack.getTag() != null && stack.getTag().contains(STORE_ITEM_TAG) ? Block.stateById(stack.getTag().getInt(STORE_ITEM_TAG)) : Blocks.AIR.defaultBlockState();
    }

    public CompoundTag getStoredTileData(ItemStack stack) {
        return stack.getTag() != null && stack.getTag().contains(TILE_DATA_TAG) ? stack.getTag().getCompound(TILE_DATA_TAG) : null;
    }
}