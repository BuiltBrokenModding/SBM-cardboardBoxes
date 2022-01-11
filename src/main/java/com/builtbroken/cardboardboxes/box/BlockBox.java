package com.builtbroken.cardboardboxes.box;

import com.builtbroken.cardboardboxes.Cardboardboxes;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

/**
 * Block for the box
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/28/2015.
 */
public class BlockBox extends BlockContainer
{
    public static final String STORE_ITEM_TAG = "storedItem";
    public static final String TILE_DATA_TAG = "tileData";

    public BlockBox()
    {
        super(Material.WOOD);
        this.setRegistryName(Cardboardboxes.DOMAIN, "cardboardbox");
        this.setTranslationKey(Cardboardboxes.PREFIX + "cardboardbox");
        this.setCreativeTab(CreativeTabs.DECORATIONS);
        this.setHardness(2f);
        this.setResistance(2f);
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn)
    {
        if (!worldIn.isRemote)
        {
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if (tileEntity instanceof TileEntityBox && ((TileEntityBox) tileEntity).getItemForPlacement() != null)
            {
                TileEntityBox tileBox = (TileEntityBox) tileEntity;
                Block block = Block.getBlockFromItem(tileBox.getItemForPlacement().getItem());
                int meta = tileBox.getItemForPlacement().getItemDamage();
                if (block != null && worldIn.setBlockState(pos, block.getStateFromMeta(meta), 3))
                {
                    NBTTagCompound compound = tileBox.getDataForPlacement();
                    if (compound != null)
                    {
                        TileEntity tile = worldIn.getTileEntity(pos);
                        if (tile != null)
                        {
                            tile.readFromNBT(compound);
                            tile.setPos(pos);
                        }
                    }
                    if (!playerIn.capabilities.isCreativeMode)
                    {
                        ItemStack stack = new ItemStack(this);
                        if (playerIn.inventory.addItemStackToInventory(stack))
                        {
                            playerIn.entityDropItem(stack, 0F);
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (worldIn.isRemote)
        {
            return true;
        }
        if (playerIn.isSneaking())
        {
            ItemStack stack = toItemStack(worldIn, pos);
            if (stack != null)
            {
                if (playerIn.inventory.addItemStackToInventory(stack))
                {
                    playerIn.inventoryContainer.detectAndSendChanges();
                    worldIn.setBlockToAir(pos);
                    return true;
                }
                else
                {
                    playerIn.sendStatusMessage(new TextComponentTranslation(getTranslationKey() + ".inventoryFull.name"), true);
                    return true;
                }
            }
            else
            {
                playerIn.sendStatusMessage(new TextComponentTranslation(getTranslationKey() + ".error.stack.null"), true);
            }
        }
        return false;
    }

    public ItemStack toItemStack(World world, BlockPos pos)
    {
        ItemStack stack = new ItemStack(Cardboardboxes.blockBox);

        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityBox)
        {
            if (((TileEntityBox) tile).getItemForPlacement() != null)
            {
                stack.setTagCompound(new NBTTagCompound());

                stack.getTagCompound().setTag(STORE_ITEM_TAG, ((TileEntityBox) tile).getItemForPlacement().writeToNBT(new NBTTagCompound()));
                if (((TileEntityBox) tile).getDataForPlacement() != null)
                {
                    stack.getTagCompound().setTag(TILE_DATA_TAG, ((TileEntityBox) tile).getDataForPlacement());
                }
            }
            else
            {
                System.out.println("Error: tile does not have an ItemStack");
            }
        }
        return stack;
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
    {
        return toItemStack(world, pos);
    }

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
    {
        return new TileEntityBox();
    }
}
