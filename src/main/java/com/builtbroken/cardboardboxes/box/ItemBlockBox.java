package com.builtbroken.cardboardboxes.box;

import com.builtbroken.cardboardboxes.Cardboardboxes;
import com.builtbroken.cardboardboxes.handler.HandlerManager;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Dark on 7/28/2015.
 */
public class ItemBlockBox extends ItemBlock {
    public ItemBlockBox(Block block) {
        super(block);
        this.setHasSubtypes(true);
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        return stack.hasTagCompound() ? 1 : 64;
    }

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        Block block = worldIn.getBlockState(pos).getBlock();
        ItemStack storedStack = getStoredBlock(stack);
        if (storedStack != null) {
            if (worldIn.isRemote) {
                return EnumActionResult.PASS;
            }
            if (block == Blocks.SNOW_LAYER) {
                pos = pos.up();
            } else if (block != Blocks.VINE && block != Blocks.TALLGRASS && block != Blocks.DEADBUSH && !block.isReplaceable(worldIn, pos)) {
                switch (facing) {
                    case DOWN:
                        pos = pos.down();
                        break;
                    case UP:
                        pos = pos.up();
                        break;
                    case NORTH:
                        pos = pos.north();
                        break;
                    case WEST:
                        pos = pos.west();
                        break;
                    case SOUTH:
                        pos = pos.south();
                        break;
                    case EAST:
                        pos = pos.east();
                        break;
                }
            }
            Block storedBlock = Block.getBlockFromItem(storedStack.getItem());
            if (stack.stackSize == 0) {
                return EnumActionResult.FAIL;
            } else if (!player.canPlayerEdit(pos, facing, stack)) {
                return EnumActionResult.FAIL;
            } else if (pos.getY() == 255 && storedBlock.isBlockSolid(worldIn, pos, facing)) {
                return EnumActionResult.FAIL;
            } else if (worldIn.canBlockBePlaced(storedBlock, pos, false, facing, player, storedStack)) {
                int storedMeta = Math.max(0, Math.min(storedStack.getItemDamage(), 16));
                IBlockState state = storedBlock.onBlockPlaced(worldIn, pos, facing, hitX, hitY, hitZ, storedMeta, player);
                if (worldIn.setBlockState(pos, storedBlock.getDefaultState(), 3)) {
                    storedBlock.onBlockPlacedBy(worldIn, pos, state, player, storedStack);
                    storedBlock.onBlockPlaced(worldIn, pos, facing, hitX, hitY, hitZ, storedMeta, player);
                    NBTTagCompound nbtTagCompound = getStoredTileData(stack);
                    if (nbtTagCompound != null) {
                        TileEntity tileEntity = worldIn.getTileEntity(pos);
                        if (tileEntity != null) {
                            tileEntity.readFromNBT(nbtTagCompound);
                            tileEntity.setPos(pos);
                        }
                    }
                    worldIn.playSound(player, pos, storedBlock.getSoundType().getStepSound(), SoundCategory.BLOCKS, (storedBlock.getSoundType().volume + 1.0F) / 2.0F, storedBlock.getSoundType().pitch * 0.8F);
                    if (!player.capabilities.isCreativeMode) {
                        stack.stackSize--;
                    }
                    if (!player.inventory.addItemStackToInventory(new ItemStack(Cardboardboxes.blockBox))) {
                        player.entityDropItem(new ItemStack(Cardboardboxes.blockBox), 0F);
                    }
                    player.inventoryContainer.detectAndSendChanges();
                    return EnumActionResult.PASS;
                }
            }
        } else if (!(block instanceof BlockBox)) {
            if (worldIn.isRemote) {
                return EnumActionResult.PASS;
            }
            HandlerManager.CanPickUpResult result = Cardboardboxes.boxHandler.canPickUp(worldIn, pos);
            if (result == HandlerManager.CanPickUpResult.CAN_PICK_UP) {
                TileEntity tileEntity = worldIn.getTileEntity(pos);
                if (tileEntity != null) {
                    ItemStack blockStack = block.getItem(worldIn, pos, worldIn.getBlockState(pos));
                    NBTTagCompound nbtTagCompound = new NBTTagCompound();
                    tileEntity.writeToNBT(nbtTagCompound);
                    nbtTagCompound.removeTag("x");
                    nbtTagCompound.removeTag("y");
                    nbtTagCompound.removeTag("z");
                    worldIn.removeTileEntity(pos);
                    worldIn.setBlockState(pos, Cardboardboxes.blockBox.getDefaultState(), 2);
                    tileEntity = worldIn.getTileEntity(pos);
                    if (tileEntity instanceof TileBox) {
                        TileBox tileBox = (TileBox) tileEntity;
                        tileBox.storedItem = blockStack;
                        tileBox.tileData = nbtTagCompound;
                        if (!player.capabilities.isCreativeMode) {
                            stack.stackSize--;
                        }
                        return EnumActionResult.PASS;
                    }
                } else {
                    player.addChatComponentMessage(new TextComponentTranslation(getUnlocalizedName() + ".noData.name"));
                }
            } else if (result == HandlerManager.CanPickUpResult.BANNED_TILE) {
                player.addChatComponentMessage(new TextComponentTranslation(getUnlocalizedName() + ".banned.tile.name"));
            } else if (result == HandlerManager.CanPickUpResult.BANNED_BLOCK) {
                player.addChatComponentMessage(new TextComponentTranslation(getUnlocalizedName() + ".banned.block.name"));
            } else {
                player.addChatComponentMessage(new TextComponentTranslation(getUnlocalizedName() + ".noData.name"));
            }
            return EnumActionResult.PASS;
        }
        return EnumActionResult.FAIL;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b) {
        if (stack.getTagCompound() != null && stack.getTagCompound().hasKey(BlockBox.STORE_ITEM_TAG)) {
            ItemStack storedStack = ItemStack.loadItemStackFromNBT(stack.getTagCompound().getCompoundTag(BlockBox.STORE_ITEM_TAG));
            String name = storedStack.getDisplayName();
            if (name != null && !name.isEmpty()) {
                list.add(name);
            } else {
                list.add("" + storedStack);
            }
        }
    }

    public ItemStack getStoredBlock(ItemStack stack) {
        return stack.getTagCompound() != null && stack.getTagCompound().hasKey(BlockBox.STORE_ITEM_TAG) ? ItemStack.loadItemStackFromNBT(stack.getTagCompound().getCompoundTag(BlockBox.STORE_ITEM_TAG)) : null;
    }

    public NBTTagCompound getStoredTileData(ItemStack stack) {
        return stack.getTagCompound() != null && stack.getTagCompound().hasKey(BlockBox.TILE_DATA_TAG) ? stack.getTagCompound().getCompoundTag(BlockBox.TILE_DATA_TAG) : null;
    }
}
