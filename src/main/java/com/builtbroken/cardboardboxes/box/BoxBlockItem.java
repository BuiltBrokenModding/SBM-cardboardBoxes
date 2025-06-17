package com.builtbroken.cardboardboxes.box;

import java.util.Optional;
import java.util.function.Consumer;

import com.builtbroken.cardboardboxes.Cardboardboxes;
import com.builtbroken.cardboardboxes.handler.CanPickUpResult;
import com.builtbroken.cardboardboxes.handler.Handler;
import com.builtbroken.cardboardboxes.handler.HandlerManager;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;

import static com.builtbroken.cardboardboxes.box.BoxBlock.BLOCK_ENTITY_DATA_TAG;
import static com.builtbroken.cardboardboxes.box.BoxBlock.STORE_ITEM_TAG;

/**
 * ItemBlock for the box
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/28/2015.
 */
public class BoxBlockItem extends BlockItem {
    public final DyeColor color;
    public BoxBlockItem(Block block, DyeColor color, Item.Properties properties) {
        super(block, properties);
        this.color = color;
    }

    //TODO add property to change render if contains item
    //TODO add property to change render based on content (e.g. show chest on box)
    //TODO add property to change render color, label, etc

    @Override
    public InteractionResult useOn(UseOnContext context) {
        //Run all logic server side
        Level level = context.getLevel();
        if (level.isClientSide) {
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
                return tryToPickupBlock(player, level, context.getClickedPos(), hand, context.getClickedFace());
            }
        }
        return InteractionResult.FAIL;
    }

    protected InteractionResult tryToPickupBlock(Player player, Level level, BlockPos pos, InteractionHand hand, Direction direction) {
        //Check that we can pick up block
        CanPickUpResult result = HandlerManager.INSTANCE.canPickUp(level, pos);

        if (result == CanPickUpResult.CAN_PICK_UP) {
            //Get tile, ignore anything without a block entity
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity != null) {
                //Get stack
                final BlockState state = level.getBlockState(pos);
                //Copy block entity data
                CompoundTag tag = blockEntity.saveWithFullMetadata(level.registryAccess());

                //Remove block entity
                level.removeBlockEntity(pos);

                //Replace block with our block
                level.setBlock(pos, getBlock().defaultBlockState(), 2);

                //Get our block entity
                if (level.getBlockEntity(pos) instanceof BoxBlockEntity boxBlockEntity) {
                    //Move data into block entity
                    boxBlockEntity.setStateForPlacement(state);
                    boxBlockEntity.setDataForPlacement(Optional.of(tag));

                    //Consume item
                    player.getItemInHand(hand).shrink(1);

                    //Done
                    return InteractionResult.SUCCESS;
                }
            } else {
                player.displayClientMessage(Component.translatable(Cardboardboxes.BOX_BLOCK.get().getDescriptionId() + ".noData"), true);
            }
        } else if (result == CanPickUpResult.BANNED_BLOCK_ENTITY) {
            player.displayClientMessage(Component.translatable(Cardboardboxes.BOX_BLOCK.get().getDescriptionId() + ".banned.tile"), true);
        } else if (result == CanPickUpResult.BANNED_BLOCK) {
            player.displayClientMessage(Component.translatable(Cardboardboxes.BOX_BLOCK.get().getDescriptionId() + ".banned.block"), true);
        } else {
            player.displayClientMessage(Component.translatable(Cardboardboxes.BOX_BLOCK.get().getDescriptionId() + ".noData"), true);
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
        final CompoundTag storedBlockEntityData = getStoredBlockEntityData(heldItemStack);
        //Check if we can place the given block
        if (storedBlockState != null && context.getPlayer().mayUseItemAt(pos, context.getClickedFace(), heldItemStack) && context.getLevel().getBlockState(pos).canBeReplaced()) {
            Handler handler = HandlerManager.INSTANCE.getHandler(storedBlockState.getBlock());
            BlockState blockstate = storedBlockState.getBlock().getStateForPlacement(context);
            //Allow handler to control placement
            if (handler != null && handler.placeBlock(context.getPlayer(), context.getLevel(), pos, hand, context.getClickedFace(), hitX, hitY, hitZ, storedBlockState, storedBlockEntityData)
                    //Run normal placement if we don't have a handler or it didn't do anything
                    || placeBlock(context, blockstate)) {
                //Get placed block
                blockstate = context.getLevel().getBlockState(pos);

                //Allow handle to do post placement modification (e.g. fix rotation)
                if (handler != null) {
                    handler.postPlaceBlock(context.getPlayer(), context.getLevel(), pos, hand, context.getClickedFace(), hitX, hitY, hitZ, storedBlockState, storedBlockEntityData);
                }

                //Set tile entity data
                if (storedBlockEntityData != null) {
                    BlockEntity blockEntity = context.getLevel().getBlockEntity(pos);
                    if (blockEntity != null) {
                        try (ProblemReporter.ScopedCollector problemReporter = new ProblemReporter.ScopedCollector(blockEntity.problemPath(), Cardboardboxes.LOGGER)) {
                            ValueInput valueInput = TagValueInput.create(problemReporter, context.getLevel().registryAccess(), storedBlockEntityData);

                            if (handler != null) {
                                handler.loadData(blockEntity, valueInput);
                            }
                            else {
                                blockEntity.loadWithComponents(valueInput);
                            }
                        }
                    }
                }


                //Place audio
                SoundType soundtype = blockstate.getBlock().getSoundType(blockstate, context.getLevel(), pos, context.getPlayer());
                context.getLevel().playSound(null, pos, soundtype.getPlaceSound(), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);

                //Consume item
                heldItemStack.shrink(1);

                //Return empty box
                if (!context.getPlayer().isCreative()) {
                    context.getPlayer().getInventory().placeItemBackInInventory(new ItemStack(getBlock()));
                }
            }

            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.FAIL;
        }
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return stack.has(DataComponents.CUSTOM_DATA) ? 1 : 64;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext ctx, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);

        if (data != null && data.contains(STORE_ITEM_TAG)) {
            data.getUnsafe().getInt(STORE_ITEM_TAG).ifPresent(id -> {
                BlockState state = Block.stateById(id);
                tooltipAdder.accept(Component.translatable(state.getBlock().getDescriptionId()));
            });
        }
    }

    public BlockState getStoredBlock(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data != null ? data.getUnsafe().getInt(STORE_ITEM_TAG).map(Block::stateById).orElse(Blocks.AIR.defaultBlockState()):Blocks.AIR.defaultBlockState();
    }

    public CompoundTag getStoredBlockEntityData(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data != null ? data.getUnsafe().getCompound(BLOCK_ENTITY_DATA_TAG).orElse(null) : null;
    }
}