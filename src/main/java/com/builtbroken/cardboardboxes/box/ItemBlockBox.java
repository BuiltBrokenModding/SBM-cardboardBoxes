package com.builtbroken.cardboardboxes.box;

import com.builtbroken.cardboardboxes.Cardboardboxes;
import com.builtbroken.cardboardboxes.handler.HandlerManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

/**
 * Created by Dark on 7/28/2015.
 */
public class ItemBlockBox extends ItemBlock
{
    public ItemBlockBox(Block block)
    {
        super(block);
        this.setHasSubtypes(true);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b)
    {
        if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("storedItem"))
        {
            ItemStack storedStack = ItemStack.loadItemStackFromNBT(stack.getTagCompound().getCompoundTag("storedItem"));
            String name = storedStack.getDisplayName();
            if (name != null && !name.isEmpty())
                list.add(name);
            else
                list.add("" + storedStack);
        }
    }

    @Override
    public int getItemStackLimit(ItemStack stack)
    {
        if (stack.hasTagCompound())
            return 1;
        return this.getItemStackLimit();
    }

    public ItemStack getStoredBlock(ItemStack stack)
    {
        return stack.getTagCompound() != null && stack.getTagCompound().hasKey("storedItem") ? ItemStack.loadItemStackFromNBT(stack.getTagCompound().getCompoundTag("storedItem")) : null;
    }

    @Override
    public boolean onItemUse(ItemStack boxItemStack, EntityPlayer player, World world, int x, int y, int z, int side, float xHit, float yHit, float zHit)
    {
        Block block = world.getBlock(x, y, z);
        ItemStack storedStack = getStoredBlock(boxItemStack);
        if (storedStack != null)
        {
            if (world.isRemote)
                return true;
            Block storedBlock = Block.getBlockFromItem(storedStack.getItem());
            int storedMeta = Math.max(0, Math.min(storedStack.getItemDamage(), 16));
            NBTTagCompound nbt = storedStack.getTagCompound() != null && storedStack.getTagCompound().hasKey("tileData") ? storedStack.getTagCompound().getCompoundTag("tileData") : null;
            if (block == Blocks.snow_layer && (world.getBlockMetadata(x, y, z) & 7) < 1)
            {
                side = 1;
            }
            else if (block != Blocks.vine && block != Blocks.tallgrass && block != Blocks.deadbush && !block.isReplaceable(world, x, y, z))
            {
                if (side == 0)
                {
                    --y;
                }

                if (side == 1)
                {
                    ++y;
                }

                if (side == 2)
                {
                    --z;
                }

                if (side == 3)
                {
                    ++z;
                }

                if (side == 4)
                {
                    --x;
                }

                if (side == 5)
                {
                    ++x;
                }
            }

            if (boxItemStack.stackSize == 0)
            {
                return false;
            }
            else if (!player.canPlayerEdit(x, y, z, side, boxItemStack))
            {
                return false;
            }
            else if (y == 255 && storedBlock.getMaterial().isSolid())
            {
                return false;
            }
            else if (world.canPlaceEntityOnSide(storedBlock, x, y, z, false, side, player, storedStack))
            {
                int meta = storedBlock.onBlockPlaced(world, x, y, z, side, xHit, yHit, zHit, storedMeta);
                if (world.setBlock(x, y, z, storedBlock, meta, 3))
                {
                    if (nbt != null)
                    {
                        TileEntity tile = world.getTileEntity(x, y, z);
                        if (tile != null)
                        {
                            tile.readFromNBT(nbt);
                            tile.xCoord = x;
                            tile.yCoord = y;
                            tile.zCoord = z;
                        }
                    }
                    storedBlock.onBlockPlacedBy(world, x, y, z, player, storedStack);
                    storedBlock.onPostBlockPlaced(world, x, y, z, meta);
                    world.playSoundEffect((double) ((float) x + 0.5F), (double) ((float) y + 0.5F), (double) ((float) z + 0.5F), storedBlock.stepSound.func_150496_b(), (storedBlock.stepSound.getVolume() + 1.0F) / 2.0F, storedBlock.stepSound.getPitch() * 0.8F);
                    --boxItemStack.stackSize;
                    if (!player.inventory.addItemStackToInventory(new ItemStack(Cardboardboxes.blockBox)))
                    {
                        player.entityDropItem(new ItemStack(Cardboardboxes.blockBox), 0f);
                    }
                }
                return true;
            }
        }
        else if (!(block instanceof BlockBox))
        {
            if (world.isRemote)
                return true;
            HandlerManager.CanPickUpResult result = Cardboardboxes.boxHandler.canPickUp(world, x, y, z);
            if (result == HandlerManager.CanPickUpResult.CAN_PICK_UP)
            {
                TileEntity tile = world.getTileEntity(x, y, z);
                if (tile != null)
                {
                    ItemStack boxStack = new ItemStack(block, 1, block.getDamageValue(world, x, y, z));
                    NBTTagCompound nbt = new NBTTagCompound();
                    tile.writeToNBT(nbt);
                    nbt.removeTag("id");
                    nbt.removeTag("x");
                    nbt.removeTag("y");
                    nbt.removeTag("z");
                    boxStack.setTagCompound(new NBTTagCompound());
                    boxStack.getTagCompound().setTag("tileData", nbt);
                    world.removeTileEntity(x, y, z);
                    world.setBlock(x, y, z, Cardboardboxes.blockBox, 0, 3);
                    tile = world.getTileEntity(x, y, z);
                    if (tile instanceof TileBox)
                    {
                        ((TileBox) tile).storedItem = boxStack;
                        if (!player.capabilities.isCreativeMode)
                        {
                            boxItemStack.stackSize--;
                        }
                        return true;
                    }
                }
            }
            else if (result == HandlerManager.CanPickUpResult.BANNED_TILE)
            {
                player.addChatComponentMessage(new ChatComponentText(StatCollector.translateToLocal(getUnlocalizedName() + ".banned.tile.name")));
            }
            else if (result == HandlerManager.CanPickUpResult.BANNED_BLOCK)
            {
                player.addChatComponentMessage(new ChatComponentText(StatCollector.translateToLocal(getUnlocalizedName() + ".banned.block.name")));
            }
            else
            {
                player.addChatComponentMessage(new ChatComponentText(StatCollector.translateToLocal(getUnlocalizedName() + ".noData.name")));
            }
            //TODO add custom message support for blocks, for example EnchantingTable "Its a bit to fragile for this box"
            return true;
        }
        return false;
    }
}
