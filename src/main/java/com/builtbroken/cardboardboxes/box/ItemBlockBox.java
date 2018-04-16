package com.builtbroken.cardboardboxes.box;

import com.builtbroken.cardboardboxes.Cardboardboxes;
import com.builtbroken.cardboardboxes.handler.CanPickUpResult;
import com.builtbroken.cardboardboxes.handler.HandlerManager;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
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

import javax.annotation.Nullable;
import java.util.List;

import static com.builtbroken.cardboardboxes.box.BlockBox.STORE_ITEM_TAG;
import static com.builtbroken.cardboardboxes.box.BlockBox.TILE_DATA_TAG;

/**
 * ItemBlock for the box
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/28/2015.
 */
public class ItemBlockBox extends ItemBlock
{
    public ItemBlockBox(Block block)
    {
        super(block);
        this.setHasSubtypes(true);
        setRegistryName("cardboardbox");
    }

    @Override
    public int getItemStackLimit(ItemStack stack)
    {
        return stack.hasTagCompound() ? 1 : 64;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        if (stack.getTagCompound() != null && stack.getTagCompound().hasKey(STORE_ITEM_TAG))
        {
            ItemStack storedStack = new ItemStack(stack.getTagCompound().getCompoundTag(STORE_ITEM_TAG));
            String name = storedStack.getDisplayName();
            if (name != null && !name.isEmpty())
            {
                tooltip.add(name);
            }
            else
            {
                tooltip.add("" + storedStack);
            }
        }
    }

    public ItemStack getStoredBlock(ItemStack stack)
    {
        return stack.getTagCompound() != null && stack.getTagCompound().hasKey(STORE_ITEM_TAG) ? new ItemStack(stack.getTagCompound().getCompoundTag(STORE_ITEM_TAG)) : ItemStack.EMPTY;
    }

    public NBTTagCompound getStoredTileData(ItemStack stack)
    {
        return stack.getTagCompound() != null && stack.getTagCompound().hasKey(TILE_DATA_TAG) ? stack.getTagCompound().getCompoundTag(TILE_DATA_TAG) : null;
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        Block block = worldIn.getBlockState(pos).getBlock();
        ItemStack stack = player.getHeldItem(hand);
        ItemStack storedStack = getStoredBlock(player.getHeldItem(hand));
        if (!storedStack.isEmpty())
        {
            //TODO check if required
            if (worldIn.isRemote)
            {
                return EnumActionResult.PASS;
            }

            //Offset block
            if (!block.isReplaceable(worldIn, pos))
            {
                pos = pos.offset(facing);
            }

            Block storedBlock = Block.getBlockFromItem(storedStack.getItem());
            if (stack.getCount() == 0)
            {
                return EnumActionResult.FAIL;
            }
            else if (!player.canPlayerEdit(pos, facing, stack))
            {
                return EnumActionResult.FAIL;
            }
            else if (pos.getY() == 255 && storedBlock.isFullCube(worldIn.getBlockState(pos)))
            {
                return EnumActionResult.FAIL;
            }
            else if (worldIn.mayPlace(storedBlock, pos, false, facing, player))
            {
                IBlockState state = worldIn.getBlockState(pos);
                if (worldIn.setBlockState(pos, storedBlock.getDefaultState(), 3))
                {
                    player.setHeldItem(hand, new ItemStack(this));
                    storedBlock.onBlockPlacedBy(worldIn, pos, state, player, storedStack);
                    worldIn.getBlockState(pos);

                    //Set tile entity data
                    NBTTagCompound nbtTagCompound = getStoredTileData(stack);
                    if (nbtTagCompound != null)
                    {
                        TileEntity tileEntity = worldIn.getTileEntity(pos);
                        if (tileEntity != null)
                        {
                            tileEntity.readFromNBT(nbtTagCompound);
                            tileEntity.setPos(pos);
                        }
                    }

                    //Play sound
                    SoundType soundtype = worldIn.getBlockState(pos).getBlock().getSoundType(worldIn.getBlockState(pos), worldIn, pos, player);
                    worldIn.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);

                    //Consume box
                    if (!player.capabilities.isCreativeMode) //TODO check if needed
                    {
                        stack.shrink(1);
                    }

                    //Return empty box
                    if (!player.inventory.addItemStackToInventory(new ItemStack(Cardboardboxes.blockBox)))
                    {
                        player.entityDropItem(new ItemStack(Cardboardboxes.blockBox), 0F);
                    }

                    //Update inventory
                    player.inventoryContainer.detectAndSendChanges();

                    //Done
                    return EnumActionResult.SUCCESS;
                }
            }
        }
        else if (!(block instanceof BlockBox))
        {
            if (worldIn.isRemote)
            {
                return EnumActionResult.SUCCESS;
            }
            CanPickUpResult result = HandlerManager.INSTANCE.canPickUp(worldIn, pos);
            if (result == CanPickUpResult.CAN_PICK_UP)
            {
                TileEntity tileEntity = worldIn.getTileEntity(pos);
                if (tileEntity != null)
                {
                    ItemStack blockStack = block.getItem(worldIn, pos, worldIn.getBlockState(pos));

                    NBTTagCompound nbtTagCompound = new NBTTagCompound();
                    tileEntity.writeToNBT(nbtTagCompound);
                    nbtTagCompound.removeTag("x");
                    nbtTagCompound.removeTag("y");
                    nbtTagCompound.removeTag("z");

                    worldIn.removeTileEntity(pos);
                    worldIn.setBlockState(pos, Cardboardboxes.blockBox.getDefaultState(), 2);

                    tileEntity = worldIn.getTileEntity(pos);
                    if (tileEntity instanceof TileEntityBox)
                    {
                        TileEntityBox tileBox = (TileEntityBox) tileEntity;
                        tileBox.setItemForPlacement(blockStack);
                        tileBox.setDataForPlacement(nbtTagCompound);
                        if (!player.capabilities.isCreativeMode)
                        {
                            stack.shrink(1);
                        }
                        return EnumActionResult.SUCCESS;
                    }
                }
                else
                {
                    player.sendStatusMessage(new TextComponentTranslation(getUnlocalizedName() + ".noData.name"), true);
                }
            }
            else if (result == CanPickUpResult.BANNED_TILE)
            {
                player.sendStatusMessage(new TextComponentTranslation(getUnlocalizedName() + ".banned.tile.name"), true);
            }
            else if (result == CanPickUpResult.BANNED_BLOCK)
            {
                player.sendStatusMessage(new TextComponentTranslation(getUnlocalizedName() + ".banned.block.name"), true);
            }
            else
            {
                player.sendStatusMessage(new TextComponentTranslation(getUnlocalizedName() + ".noData.name"), true);
            }
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.FAIL;
    }
}