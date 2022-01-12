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
        this.setRegistryName(Cardboardboxes.DOMAIN, "cardboardbox");
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void attack(BlockState state, Level worldIn, BlockPos pos, Player player) {
        if (!worldIn.isClientSide) {
            if (worldIn.getBlockEntity(pos) instanceof BoxBlockEntity tileBox && tileBox.getStateForPlacement() != null) {
                if (tileBox.getStateForPlacement() != null && worldIn.setBlock(pos, tileBox.getStateForPlacement(), 3)) {
                    CompoundTag compound = tileBox.getDataForPlacement();
                    if (compound != null) {
                        BlockEntity blockEntity = worldIn.getBlockEntity(pos);
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
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (worldIn.isClientSide) {
            return InteractionResult.PASS;
        }
        if (player.isShiftKeyDown()) {
            ItemStack stack = toItemStack(worldIn, pos);
            if (stack != null) {
                if (player.getInventory().add(stack)) {
                    player.getInventory().setChanged();
                    worldIn.removeBlock(pos, false);
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

    public ItemStack toItemStack(BlockGetter world, BlockPos pos) {
        ItemStack stack = new ItemStack(Cardboardboxes.boxBlock);

        if (world.getBlockEntity(pos) instanceof BoxBlockEntity tile) {
            if (tile.getStateForPlacement() != null) {
                stack.setTag(new CompoundTag());

                stack.getTag().putInt(STORE_ITEM_TAG, Block.getId(tile.getStateForPlacement()));
                if (tile.getDataForPlacement() != null) {
                    stack.getTag().put(BLOCK_ENTITY_DATA_TAG, tile.getDataForPlacement());
                }
            } else {
                System.out.println("Error: tile does not have an ItemStack");
            }
        }
        return stack;
    }

    @Override
    public ItemStack getPickBlock(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
        return toItemStack(world, pos);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BoxBlockEntity(pos, state);
    }
}