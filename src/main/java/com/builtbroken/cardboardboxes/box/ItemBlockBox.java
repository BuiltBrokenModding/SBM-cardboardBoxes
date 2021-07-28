package com.builtbroken.cardboardboxes.box;

import com.builtbroken.cardboardboxes.Cardboardboxes;
import com.builtbroken.cardboardboxes.handler.CanPickUpResult;
import com.builtbroken.cardboardboxes.handler.Handler;
import com.builtbroken.cardboardboxes.handler.HandlerManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import java.util.List;

import static com.builtbroken.cardboardboxes.box.BlockBox.STORE_ITEM_TAG;
import static com.builtbroken.cardboardboxes.box.BlockBox.TILE_DATA_TAG;

/**
 * ItemBlock for the box
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/28/2015.
 */
public class ItemBlockBox extends BlockItem {
    public ItemBlockBox(Block block) {
        super(block, new Item.Properties().tab(ItemGroup.TAB_DECORATIONS));
        this.setRegistryName(block.getRegistryName());
    }

    //TODO add property to change render if contains item
    //TODO add property to change render based on content (e.g. show chest on box)
    //TODO add property to change render color, label, etc

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        //Run all logic server side
        World worldIn = context.getLevel();
        if (worldIn.isClientSide) {
            return ActionResultType.SUCCESS;
        }

        PlayerEntity player = context.getPlayer();
        Hand hand = context.getHand();
        final ItemStack heldItemStack = context.getItemInHand();
        if (!heldItemStack.isEmpty()) {
            final BlockState storeBlock = getStoredBlock(heldItemStack);
            if (storeBlock.getBlock() != Blocks.AIR) {
                return tryToPlaceBlock(new BlockItemUseContext(context));
            } else {
                return tryToPickupBlock(player, worldIn, context.getClickedPos(), hand, context.getClickedFace());
            }
        }
        return ActionResultType.FAIL;
    }

    protected ActionResultType tryToPickupBlock(PlayerEntity player, World worldIn, BlockPos pos, Hand hand, Direction direction) {
        //Check that we can pick up block
        CanPickUpResult result = HandlerManager.INSTANCE.canPickUp(worldIn, pos);

        if (result == CanPickUpResult.CAN_PICK_UP) {
            //Get tile, ignore anything without a tile
            TileEntity tileEntity = worldIn.getBlockEntity(pos);
            if (tileEntity != null) {
                //Get stack
                final BlockState state = worldIn.getBlockState(pos);
                //Copy tile data
                CompoundNBT nbtTagCompound = new CompoundNBT();
                tileEntity.save(nbtTagCompound);

                //Remove location data
                nbtTagCompound.remove("x");
                nbtTagCompound.remove("y");
                nbtTagCompound.remove("z");

                //Remove tile
                worldIn.removeBlockEntity(pos);

                //Replace block with our block
                worldIn.setBlock(pos, Cardboardboxes.blockBox.defaultBlockState(), 2);

                //Get our tile
                tileEntity = worldIn.getBlockEntity(pos);
                if (tileEntity instanceof TileEntityBox) {
                    TileEntityBox tileBox = (TileEntityBox) tileEntity;

                    //Move data into tile
                    tileBox.setStateForPlacement(state);
                    tileBox.setDataForPlacement(nbtTagCompound);

                    //Consume item
                    player.getItemInHand(hand).shrink(1);

                    //Done
                    return ActionResultType.SUCCESS;
                }
            } else {
                player.displayClientMessage(new TranslationTextComponent(getDescriptionId() + ".noData"), true);
            }
        } else if (result == CanPickUpResult.BANNED_TILE) {
            player.displayClientMessage(new TranslationTextComponent(getDescriptionId() + ".banned.tile"), true);
        } else if (result == CanPickUpResult.BANNED_BLOCK) {
            player.displayClientMessage(new TranslationTextComponent(getDescriptionId() + ".banned.block"), true);
        } else {
            player.displayClientMessage(new TranslationTextComponent(getDescriptionId() + ".noData"), true);
        }
        return ActionResultType.SUCCESS;
    }

    protected ActionResultType tryToPlaceBlock(BlockItemUseContext context) {
        BlockPos pos = context.getClickedPos();
        Hand hand = context.getHand();
        //Move up one if not replaceable
        float hitX = (float) context.getClickLocation().x(), hitY = (float) context.getClickLocation().y(), hitZ = (float) context.getClickLocation().z();
        if (!context.canPlace()) {
            pos = pos.relative(context.getClickedFace());
        }

        final ItemStack heldItemStack = context.getItemInHand();
        final BlockState storedBlockState = getStoredBlock(heldItemStack);
        final CompoundNBT savedTileData = getStoredTileData(heldItemStack);
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
                    TileEntity tileEntity = context.getLevel().getBlockEntity(pos);
                    if (tileEntity != null) {
                        if (handler != null) {
                            handler.loadData(blockstate, tileEntity, savedTileData);
                        } else {
                            tileEntity.load(blockstate, savedTileData);
                        }
                        tileEntity.setPosition(pos);
                    }
                }


                //Place audio
                SoundType soundtype = blockstate.getBlock().getSoundType(blockstate, context.getLevel(), pos, context.getPlayer());
                context.getLevel().playSound(context.getPlayer(), pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);

                //Consume item
                heldItemStack.shrink(1);

                //Return empty box
                if (!context.getPlayer().isCreative() && !context.getPlayer().inventory.add(new ItemStack(Cardboardboxes.blockBox))) {
                    context.getPlayer().spawnAtLocation(new ItemStack(Cardboardboxes.blockBox), 0F);
                }
            }

            return ActionResultType.SUCCESS;
        } else {
            return ActionResultType.FAIL;
        }
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        return stack.hasTag() ? 1 : 64;
    }

    @Override
    public void appendHoverText(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (stack.getTag() != null && stack.getTag().contains(STORE_ITEM_TAG)) {
            BlockState state = Block.stateById(stack.getTag().getInt(STORE_ITEM_TAG));
            {
                tooltip.add(new TranslationTextComponent(state.getBlock().getDescriptionId()));
            }
        }
    }

    public BlockState getStoredBlock(ItemStack stack) {
        return stack.getTag() != null && stack.getTag().contains(STORE_ITEM_TAG) ? Block.stateById(stack.getTag().getInt(STORE_ITEM_TAG)) : Blocks.AIR.defaultBlockState();
    }

    public CompoundNBT getStoredTileData(ItemStack stack) {
        return stack.getTag() != null && stack.getTag().contains(TILE_DATA_TAG) ? stack.getTag().getCompound(TILE_DATA_TAG) : null;
    }
}