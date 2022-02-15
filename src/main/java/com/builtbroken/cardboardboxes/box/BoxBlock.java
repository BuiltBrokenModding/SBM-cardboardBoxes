package com.builtbroken.cardboardboxes.box;

import javax.annotation.Nullable;

import com.builtbroken.cardboardboxes.Cardboardboxes;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

/**
 * Block for the box
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/28/2015.
 */
public class BoxBlock extends BaseEntityBlock {
    public static final String STORE_ITEM_TAG = "storedItem";
    public static final String BLOCK_ENTITY_DATA_TAG = "tileData";

    public BoxBlock() {
        super(Properties.of(Material.WOOD).strength(2f, 2f));
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void attack(BlockState state, Level level, BlockPos pos, Player player) {
        if (!level.isClientSide) {
            if (level.getBlockEntity(pos) instanceof BoxBlockEntity boxBlockEntity && boxBlockEntity.getStateForPlacement() != null) {
                if (boxBlockEntity.getStateForPlacement() != null && level.setBlock(pos, boxBlockEntity.getStateForPlacement(), 3)) {
                    CompoundTag compound = boxBlockEntity.getDataForPlacement();
                    if (compound != null) {
                        BlockEntity blockEntity = level.getBlockEntity(pos);
                        if (blockEntity != null) {
                            blockEntity.load(compound);
                        }
                    }
                    if (!player.isCreative()) {
                        ItemStack stack = new ItemStack(this);
                        if (player.getInventory().add(stack)) {
                            player.spawnAtLocation(stack, 0F);
                        }
                    }
                }
            }
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.PASS;
        }
        if (player.isShiftKeyDown()) {
            ItemStack stack = toItemStack(level, pos);
            if (stack != null) {
                if (player.getInventory().add(stack)) {
                    player.getInventory().setChanged();
                    level.removeBlock(pos, false);
                    return InteractionResult.SUCCESS;
                } else {
                    player.displayClientMessage(new TranslatableComponent(getDescriptionId() + ".inventoryFull"), true);
                    return InteractionResult.PASS;
                }
            } else {
                player.displayClientMessage(new TranslatableComponent(getDescriptionId() + ".error.stack.null"), true);
            }
        }
        return InteractionResult.PASS;
    }

    public ItemStack toItemStack(BlockGetter level, BlockPos pos) {
        ItemStack stack = new ItemStack(Cardboardboxes.BOX_BLOCK.get());

        if (level.getBlockEntity(pos) instanceof BoxBlockEntity blockEntity) {
            if (blockEntity.getStateForPlacement() != null) {
                stack.setTag(new CompoundTag());

                stack.getTag().putInt(STORE_ITEM_TAG, Block.getId(blockEntity.getStateForPlacement()));
                if (blockEntity.getDataForPlacement() != null) {
                    stack.getTag().put(BLOCK_ENTITY_DATA_TAG, blockEntity.getDataForPlacement());
                }
            } else {
                System.out.println("Error: block entity does not have an ItemStack");
            }
        }
        return stack;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        return toItemStack(level, pos);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BoxBlockEntity(pos, state);
    }
}