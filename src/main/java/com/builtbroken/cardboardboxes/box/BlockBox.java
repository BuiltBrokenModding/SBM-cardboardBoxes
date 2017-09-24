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
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

/**
 * Created by Dark on 7/28/2015.
 */
public class BlockBox extends BlockContainer
{
    public static final String STORE_ITEM_TAG = "storedItem";
    public static final String TILE_DATA_TAG = "tileData";
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
        {
            return top;
        }
        return this.blockIcon;
    }

    @Override
    public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player)
    {
        if (!world.isRemote)
        {
            //Get box
            TileEntity tile = world.getTileEntity(x, y, z);
            if (tile instanceof TileBox && ((TileBox) tile).storedItem != null)
            {
                //Get stored data
                Block block = Block.getBlockFromItem(((TileBox) tile).storedItem.getItem());
                int meta = ((TileBox) tile).storedItem.getItemDamage();

                //Place block if data is stored
                if (block != null && world.setBlock(x, y, z, block, meta, 3))
                {
                    //Load save data
                    NBTTagCompound nbt = ((TileBox) tile).tileData;
                    if (((TileBox) tile).tileData != null)
                    {
                        TileEntity tileEntity = world.getTileEntity(x, y, z);
                        if (tileEntity != null)
                        {
                            tileEntity.readFromNBT(nbt);
                            tileEntity.xCoord = x;
                            tileEntity.yCoord = y;
                            tileEntity.zCoord = z;
                        }
                    }

                    //If not creative mode, drop box
                    if(!player.capabilities.isCreativeMode)
                    {
                        ItemStack stack = new ItemStack(this);
                        if (player.inventory.addItemStackToInventory(stack))
                        {
                            player.entityDropItem(stack, 0f);
                        }
                    }
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

        //Pick up block if sneaking
        if (player.isSneaking())
        {
            //Convert to item
            ItemStack stack = toItemStack(world, x, y, z);
            if(stack != null)
            {
                //add to inventory if space
                if (player.inventory.addItemStackToInventory(stack))
                {
                    //Update inventory
                    player.inventoryContainer.detectAndSendChanges();
                    //Remove block
                    world.setBlockToAir(x, y, z);
                    return true;
                }
                else
                {
                    player.addChatComponentMessage(new ChatComponentText(StatCollector.translateToLocal(getUnlocalizedName() + ".inventoryFull.name")));
                    return true;
                }
            }
            else
            {
                player.addChatComponentMessage(new ChatComponentText(StatCollector.translateToLocal(getUnlocalizedName() + ".error.stack.null")));
                return true;
            }
        }
        return false;
    }

    public ItemStack toItemStack(World world, int x, int y, int z)
    {
        ItemStack stack = new ItemStack(Cardboardboxes.blockBox);

        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileBox)
        {
            if (((TileBox) tile).storedItem != null)
            {
                stack.setTagCompound(new NBTTagCompound());

                stack.getTagCompound().setTag(STORE_ITEM_TAG, ((TileBox) tile).storedItem.writeToNBT(new NBTTagCompound()));
                if (((TileBox) tile).tileData != null)
                {
                    stack.getTagCompound().setTag(TILE_DATA_TAG, ((TileBox) tile).tileData);
                }
            }
            else
            {
                System.out.println("Error tile does not have an ItemStack");
            }
        }
        return stack;
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player)
    {
        return toItemStack(world, x, y, z);
    }

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
    {
        return new TileBox();
    }
}
