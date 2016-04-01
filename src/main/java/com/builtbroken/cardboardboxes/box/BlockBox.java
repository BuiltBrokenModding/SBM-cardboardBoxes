package com.builtbroken.cardboardboxes.box;

import com.builtbroken.cardboardboxes.Cardboardboxes;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

/**
 * Created by Dark on 7/28/2015.
 */
public class BlockBox extends BlockContainer
{
    public static IIcon top;

    public BlockBox()
    {
        super(Material.wood);
        this.setBlockName(Cardboardboxes.PREFIX + "cardboardBox");
        this.setCreativeTab(CreativeTabs.tabDecorations);
        this.setHardness(2f);
        this.setResistance(2f);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg)
    {
        this.blockIcon = reg.registerIcon(Cardboardboxes.PREFIX + "box_side");
        this.top = reg.registerIcon(Cardboardboxes.PREFIX + "box_top");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        if (side == 1)
            return top;
        return this.blockIcon;
    }

    @Override
    public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player)
    {
        if (world.isRemote)
        {
            return;
        }
        //TODO remove box and place tile back down
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileBox && ((TileBox) tile).storedItem != null)
        {
            Block block = Block.getBlockFromItem(((TileBox) tile).storedItem.getItem());
            int meta = ((TileBox) tile).storedItem.getItemDamage();
            if (block != null && world.setBlock(x, y, z, block, meta, 3))
            {
                if (((TileBox) tile).storedItem.getTagCompound() != null && ((TileBox) tile).storedItem.getTagCompound().hasKey("tileData"))
                {
                    NBTTagCompound nbt = ((TileBox) tile).storedItem.getTagCompound().getCompoundTag("tileData");
                    TileEntity tileEntity = world.getTileEntity(x, y, z);
                    if (tileEntity != null)
                    {
                        tileEntity.readFromNBT(nbt);
                        tileEntity.xCoord = x;
                        tileEntity.yCoord = y;
                        tileEntity.zCoord = z;
                    }
                }
                ItemStack stack = new ItemStack(this);
                if (player.inventory.addItemStackToInventory(stack))
                {
                    player.entityDropItem(stack, 0f);
                }
            }
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float xHit, float yHit, float zHit)
    {
        if (world.isRemote)
        {
            return true;
        }
        if (player.isSneaking())
        {
            ItemStack stack = new ItemStack(Cardboardboxes.blockBox, 1, 0);

            TileEntity tile = world.getTileEntity(x, y, z);
            if (tile instanceof TileBox)
            {
                if (((TileBox) tile).storedItem != null)
                {
                    stack.setTagCompound(new NBTTagCompound());
                    stack.getTagCompound().setTag("storedItem", ((TileBox) tile).storedItem.writeToNBT(new NBTTagCompound()));
                }
                else
                {
                    System.out.println("Error tile does not have an itemstack");
                }
            }

            if (player.inventory.addItemStackToInventory(stack))
            {
                player.inventoryContainer.detectAndSendChanges();
                world.setBlockToAir(x, y, z);
                return true;
            }
            else
            {
                player.addChatComponentMessage(new ChatComponentText(StatCollector.translateToLocal(getUnlocalizedName() + ".inventoryFull.name")));
                return true;
            }
        }
        return false;
    }

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
    {
        return new TileBox();
    }
}
