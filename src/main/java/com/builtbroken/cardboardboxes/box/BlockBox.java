package com.builtbroken.cardboardboxes.box;

import com.builtbroken.cardboardboxes.Cardboardboxes;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

/**
 * Block for the box
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/28/2015.
 */
public class BlockBox extends ContainerBlock {
    public static final String STORE_ITEM_TAG = "storedItem";
    public static final String TILE_DATA_TAG = "tileData";

    public BlockBox() {
        super(Properties.of(Material.WOOD).strength(2f, 2f));
        this.setRegistryName(Cardboardboxes.DOMAIN, "cardboardbox");
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public void attack(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
        if (!worldIn.isClientSide) {
            TileEntity tileEntity = worldIn.getBlockEntity(pos);
            if (tileEntity instanceof TileEntityBox && ((TileEntityBox) tileEntity).getStateForPlacement() != null) {
                TileEntityBox tileBox = (TileEntityBox) tileEntity;
                if (tileBox.getStateForPlacement() != null && worldIn.setBlock(pos, tileBox.getStateForPlacement(), 3)) {
                    CompoundNBT compound = tileBox.getDataForPlacement();
                    if (compound != null) {
                        TileEntity tile = worldIn.getBlockEntity(pos);
                        if (tile != null) {
                            tile.load(state, compound);
                            tile.setPosition(pos);
                        }
                    }
                    if (!player.isCreative()) {
                        ItemStack stack = new ItemStack(this);
                        if (player.inventory.add(stack)) {
                            player.spawnAtLocation(stack, 0F);
                        }
                    }
                }
            }
        }
    }

    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (worldIn.isClientSide) {
            return ActionResultType.PASS;
        }
        if (player.isShiftKeyDown()) {
            ItemStack stack = toItemStack(worldIn, pos);
            if (stack != null) {
                if (player.inventory.add(stack)) {
                    player.inventory.setChanged();
                    worldIn.removeBlock(pos, false);
                    return ActionResultType.SUCCESS;
                } else {
                    player.displayClientMessage(new TranslationTextComponent(getDescriptionId() + ".inventoryFull"), true);
                    return ActionResultType.PASS;
                }
            } else {
                player.displayClientMessage(new TranslationTextComponent(getDescriptionId() + ".error.stack.null"), true);
            }
        }
        return ActionResultType.PASS;
    }

    public ItemStack toItemStack(IBlockReader world, BlockPos pos) {
        ItemStack stack = new ItemStack(Cardboardboxes.blockBox);

        TileEntity tile = world.getBlockEntity(pos);
        if (tile instanceof TileEntityBox) {
            if (((TileEntityBox) tile).getStateForPlacement() != null) {
                stack.setTag(new CompoundNBT());

                stack.getTag().putInt(STORE_ITEM_TAG, Block.getId(((TileEntityBox) tile).getStateForPlacement()));
                if (((TileEntityBox) tile).getDataForPlacement() != null) {
                    stack.getTag().put(TILE_DATA_TAG, ((TileEntityBox) tile).getDataForPlacement());
                }
            } else {
                System.out.println("Error: tile does not have an ItemStack");
            }
        }
        return stack;
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
        return toItemStack(world, pos);
    }

    @Override
    public TileEntity newBlockEntity(IBlockReader world) {
        return new TileEntityBox();
    }
}