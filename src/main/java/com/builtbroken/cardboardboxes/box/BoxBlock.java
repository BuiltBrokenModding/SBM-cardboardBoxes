package com.builtbroken.cardboardboxes.box;

import javax.annotation.Nullable;

import com.builtbroken.cardboardboxes.Cardboardboxes;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Block for the box
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/28/2015.
 */
public class BoxBlock extends BaseEntityBlock {
    public static final String STORE_ITEM_TAG = "storedItem";
    public static final String BLOCK_ENTITY_DATA_TAG = "tileData";

    public final DyeColor color;

    public BoxBlock(DyeColor color, BlockBehaviour.Properties properties) {
        super(properties.mapColor(color == null ? MapColor.DIRT : color.getMapColor()).strength(2f, 2f));
        this.color = color;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void attack(BlockState state, Level level, BlockPos pos, Player player) {
        if (!level.isClientSide()) {
            if (level.getBlockEntity(pos) instanceof BoxBlockEntity boxBlockEntity && boxBlockEntity.getStateForPlacement() != null) {
                if (level.setBlock(pos, boxBlockEntity.getStateForPlacement(), 3)) {
                    boxBlockEntity.getDataForPlacement().ifPresent(compound -> {
                        BlockEntity blockEntity = level.getBlockEntity(pos);
                        if (blockEntity != null) {
                            try (ProblemReporter.ScopedCollector problemReporter = new ProblemReporter.ScopedCollector(blockEntity.problemPath(), Cardboardboxes.LOGGER)) {
                                ValueInput valueInput = TagValueInput.create(problemReporter, level.registryAccess(), compound);
                                blockEntity.loadWithComponents(valueInput);
                            }
                        }
                    });
                    if (!player.isCreative()) {
                        ItemStack stack = new ItemStack(this);
                        if (player.getInventory().add(stack)) {
                            player.spawnAtLocation((ServerLevel) level, stack, 0F);
                        }
                    }
                }
            }
        }
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide()) {
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
                    player.displayClientMessage(Component.translatable(Cardboardboxes.BOX_BLOCK.get().getDescriptionId() + ".inventoryFull"), true);
                    return InteractionResult.PASS;
                }
            } else {
                player.displayClientMessage(Component.translatable(Cardboardboxes.BOX_BLOCK.get().getDescriptionId() + ".error.stack.null"), true);
            }
        }
        return InteractionResult.PASS;
    }

    public ItemStack toItemStack(BlockGetter level, BlockPos pos) {
        ItemStack stack = new ItemStack(this);

        if (level.getBlockEntity(pos) instanceof BoxBlockEntity blockEntity) {
            if (blockEntity.getStateForPlacement() != null) {
                CompoundTag tag = new CompoundTag();

                tag.putInt(STORE_ITEM_TAG, Block.getId(blockEntity.getStateForPlacement()));
                blockEntity.getDataForPlacement().ifPresent(data -> tag.put(BLOCK_ENTITY_DATA_TAG, data));
                CustomData.set(DataComponents.CUSTOM_DATA, stack, tag);
            } else {
                System.out.println("Error: block entity does not have an ItemStack");
            }
        }
        return stack;
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData, Player player) {
        return toItemStack(level, pos);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BoxBlockEntity(pos, state);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return null;
    }
}