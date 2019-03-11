package com.builtbroken.cardboardboxes.box;

import com.builtbroken.cardboardboxes.Cardboardboxes;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
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
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

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
    	super(Properties.create(Material.WOOD).hardnessAndResistance(2f, 2f));
        this.setRegistryName(Cardboardboxes.DOMAIN, "cardboardbox");
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean hasTileEntity() {
    	return true;
    }

    @Override
    public void onBlockClicked(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player)
    {
        if (!worldIn.isRemote)
        {
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if (tileEntity instanceof TileEntityBox && ((TileEntityBox) tileEntity).getStateForPlacement() != null)
            {
                TileEntityBox tileBox = (TileEntityBox) tileEntity;
                if (tileBox.getStateForPlacement() != null && worldIn.setBlockState(pos, tileBox.getStateForPlacement(), 3))
                {
                    NBTTagCompound compound = tileBox.getDataForPlacement();
                    if (compound != null)
                    {
                        TileEntity tile = worldIn.getTileEntity(pos);
                        if (tile != null)
                        {
                            tile.read(compound);
                            tile.setPos(pos);
                        }
                    }
                    if (!player.isCreative())
                    {
                        ItemStack stack = new ItemStack(this);
                        if (player.inventory.addItemStackToInventory(stack))
                        {
                            player.entityDropItem(stack, 0F);
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean onBlockActivated(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (worldIn.isRemote)
        {
            return true;
        }
        if (player.isSneaking())
        {
            ItemStack stack = toItemStack(worldIn, pos);
            if (stack != null)
            {
                if (player.inventory.addItemStackToInventory(stack))
                {
                    player.inventoryContainer.detectAndSendChanges();
                    worldIn.removeBlock(pos);
                    return true;
                }
                else
                {
                    player.sendStatusMessage(new TextComponentTranslation(getTranslationKey() + ".inventoryFull"), true);
                    return true;
                }
            }
            else
            {
                player.sendStatusMessage(new TextComponentTranslation(getTranslationKey() + ".error.stack.null"), true);
            }
        }
        return false;
    }

    public ItemStack toItemStack(IBlockReader world, BlockPos pos)
    {
        ItemStack stack = new ItemStack(Cardboardboxes.blockBox);

        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityBox)
        {
            if (((TileEntityBox) tile).getStateForPlacement() != null)
            {
                stack.setTag(new NBTTagCompound());

                stack.getTag().setInt(STORE_ITEM_TAG, Block.getStateId(((TileEntityBox) tile).getStateForPlacement()));
                if (((TileEntityBox) tile).getDataForPlacement() != null)
                {
                    stack.getTag().setTag(TILE_DATA_TAG, ((TileEntityBox) tile).getDataForPlacement());
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
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, EntityPlayer player)
    {
        return toItemStack(world, pos);
    }

    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn)
    {
        return new TileEntityBox();
    }
}
