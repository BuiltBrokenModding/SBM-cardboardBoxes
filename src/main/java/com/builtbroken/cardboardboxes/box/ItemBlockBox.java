package com.builtbroken.cardboardboxes.box;

import com.builtbroken.cardboardboxes.Cardboardboxes;
import com.builtbroken.cardboardboxes.handler.CanPickUpResult;
import com.builtbroken.cardboardboxes.handler.Handler;
import com.builtbroken.cardboardboxes.handler.HandlerManager;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
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
        this.setRegistryName(block.getRegistryName());
        this.setHasSubtypes(true);
    }

    //TODO add property to change render if contains item
    //TODO add property to change render based on content (e.g. show chest on box)
    //TODO add property to change render color, label, etc

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        //Run all logic server side
        if (worldIn.isRemote)
        {
            return EnumActionResult.SUCCESS;
        }

        final ItemStack heldItemStack = player.getHeldItem(hand);
        if (!heldItemStack.isEmpty())
        {
            final ItemStack storedStack = getStoredBlock(heldItemStack);
            if (!storedStack.isEmpty())
            {
                return tryToPlaceBlock(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
            }
            else
            {
                return tryToPickupBlock(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
            }
        }
        return EnumActionResult.FAIL;
    }

    protected EnumActionResult tryToPickupBlock(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        //Check that we can pick up block
        CanPickUpResult result = HandlerManager.INSTANCE.canPickUp(worldIn, pos);
        if (result == CanPickUpResult.CAN_PICK_UP)
        {
            //Get tile, ignore anything without a tile
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if (tileEntity != null)
            {
                //Get stack
                final IBlockState state = worldIn.getBlockState(pos);
                final ItemStack blockStack = state.getBlock().getItem(worldIn, pos, state);
                if (!blockStack.isEmpty())
                {
                    //Copy tile data
                    NBTTagCompound nbtTagCompound = new NBTTagCompound();
                    tileEntity.writeToNBT(nbtTagCompound);

                    //Remove location data
                    nbtTagCompound.removeTag("x");
                    nbtTagCompound.removeTag("y");
                    nbtTagCompound.removeTag("z");

                    //Remove tile
                    worldIn.removeTileEntity(pos);

                    //Replace block with our block
                    worldIn.setBlockState(pos, Cardboardboxes.blockBox.getDefaultState(), 2);

                    //Get our tile
                    tileEntity = worldIn.getTileEntity(pos);
                    if (tileEntity instanceof TileEntityBox)
                    {
                        TileEntityBox tileBox = (TileEntityBox) tileEntity;

                        //Move data into tile
                        tileBox.setItemForPlacement(blockStack);
                        tileBox.setDataForPlacement(nbtTagCompound);

                        //Consume item
                        player.getHeldItem(hand).shrink(1);

                        //Done
                        return EnumActionResult.SUCCESS;
                    }
                }
                else
                {
                    player.sendStatusMessage(new TextComponentTranslation(getUnlocalizedName() + ".noItem.name"), true);
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

    protected EnumActionResult tryToPlaceBlock(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        IBlockState iblockstate = worldIn.getBlockState(pos);
        Block block = iblockstate.getBlock();

        //Move up one if not replaceable
        if (!block.isReplaceable(worldIn, pos))
        {
            pos = pos.offset(facing);
        }

        final ItemStack heldItemStack = player.getHeldItem(hand);
        final ItemStack storeBlockAsItemStack = getStoredBlock(heldItemStack);
        final Block storedBlock = Block.getBlockFromItem(storeBlockAsItemStack.getItem());
        final NBTTagCompound savedTileData = getStoredTileData(heldItemStack);

        //Check if we can place the given block
        if (storedBlock != null && player.canPlayerEdit(pos, facing, heldItemStack) && worldIn.mayPlace(storedBlock, pos, false, facing, (Entity) null))
        {
            Handler handler = HandlerManager.INSTANCE.getHandler(storedBlock);

            int meta = this.getMetadata(heldItemStack.getMetadata());
            IBlockState blockstate = storedBlock.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, player, hand);

            //Allow handler to control placement
            if (handler != null && handler.placeBlock(player, worldIn, pos, hand, facing, hitX, hitY, hitZ, storeBlockAsItemStack, savedTileData)
                    //Run normal placement if we don't have a handler or it didn't do anything
                    || placeBlockAt(heldItemStack, player, worldIn, pos, facing, hitX, hitY, hitZ, blockstate))
            {
                //Get placed block
                blockstate = worldIn.getBlockState(pos);

                //Allow handle to do post placement modification (e.g. fix rotation)
                if (handler != null)
                {
                    handler.postPlaceBlock(player, worldIn, pos, hand, facing, hitX, hitY, hitZ, storeBlockAsItemStack, savedTileData);
                }

                //Set tile entity data
                if (savedTileData != null)
                {
                    TileEntity tileEntity = worldIn.getTileEntity(pos);
                    if (tileEntity != null)
                    {
                        if (handler != null)
                        {
                            handler.loadData(tileEntity, savedTileData);
                        }
                        else
                        {
                            tileEntity.readFromNBT(savedTileData);
                        }
                        tileEntity.setPos(pos);
                    }
                }


                //Place audio
                SoundType soundtype = blockstate.getBlock().getSoundType(blockstate, worldIn, pos, player);
                worldIn.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);

                //Consume item
                heldItemStack.shrink(1);

                //Return empty box
                if (!player.isCreative() && !player.inventory.addItemStackToInventory(new ItemStack(Cardboardboxes.blockBox)))
                {
                    player.entityDropItem(new ItemStack(Cardboardboxes.blockBox), 0F);
                }
            }

            return EnumActionResult.SUCCESS;
        }
        else
        {
            return EnumActionResult.FAIL;
        }
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
}