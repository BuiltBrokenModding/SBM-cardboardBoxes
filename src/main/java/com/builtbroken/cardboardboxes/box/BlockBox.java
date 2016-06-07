package com.builtbroken.cardboardboxes.box;

import com.builtbroken.cardboardboxes.Cardboardboxes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
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
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/**
 * Created by Dark on 7/28/2015.
 */
public class BlockBox extends BlockContainer
{
    public static final String STORE_ITEM_TAG = "storedItem";
    public static final String TILE_DATA_TAG = "tileData";

    public BlockBox()
    {
        super(Material.WOOD);
        this.setRegistryName("cardboardBox");
        this.setUnlocalizedName(Cardboardboxes.PREFIX + "cardboardBox");
        this.setCreativeTab(CreativeTabs.DECORATIONS);
        this.setHardness(2f);
        this.setResistance(2f);
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL; // this is default invisible... mojang pls
    }

    @SideOnly(Side.CLIENT)
    public void registerModel()
    {
        ModelLoader.setCustomModelResourceLocation(new ItemStack(this).getItem(), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    @Override
    public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn)
    {
        if (!worldIn.isRemote)
        {
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if (tileEntity instanceof TileBox && ((TileBox) tileEntity).storedItem != null)
            {
                TileBox tileBox = (TileBox) tileEntity;
                Block block = Block.getBlockFromItem(tileBox.storedItem.getItem());
                int meta = tileBox.storedItem.getItemDamage();
                if (block != null && worldIn.setBlockState(pos, block.getStateFromMeta(meta), 3))
                {
                    NBTTagCompound compound = tileBox.tileData;
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
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
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
                } else
                {
                    playerIn.addChatComponentMessage(new TextComponentTranslation(getUnlocalizedName() + ".inventoryFull.name"));
                    return true;
                }
            } else
            {
                playerIn.addChatComponentMessage(new TextComponentTranslation(getUnlocalizedName() + ".error.stack.null"));
            }
        }
        return false;
    }

    public ItemStack toItemStack(World world, BlockPos pos)
    {
        ItemStack stack = new ItemStack(Cardboardboxes.blockBox);

        TileEntity tile = world.getTileEntity(pos);
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
            } else
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
        return new TileBox();
    }
}
