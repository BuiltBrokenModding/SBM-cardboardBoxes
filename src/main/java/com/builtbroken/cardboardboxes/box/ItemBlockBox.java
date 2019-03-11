package com.builtbroken.cardboardboxes.box;

import com.builtbroken.cardboardboxes.Cardboardboxes;
import com.builtbroken.cardboardboxes.handler.CanPickUpResult;
import com.builtbroken.cardboardboxes.handler.Handler;
import com.builtbroken.cardboardboxes.handler.HandlerManager;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.smartcardio.Card;
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
        super(block, new Item.Properties().group(ItemGroup.DECORATIONS));
        this.setRegistryName(block.getRegistryName());
    }

    //TODO add property to change render if contains item
    //TODO add property to change render based on content (e.g. show chest on box)
    //TODO add property to change render color, label, etc

    @Override
    public EnumActionResult onItemUse(ItemUseContext ctxt)
    {
        //Run all logic server side
    	World worldIn = ctxt.getWorld();
        if (worldIn.isRemote)
        {
            return EnumActionResult.SUCCESS;
        }

    	EntityPlayer player = ctxt.getPlayer();
    	EnumHand hand = player.getActiveHand();
        final ItemStack heldItemStack = player.getHeldItem(hand);
        if (!heldItemStack.isEmpty())
        {
            final IBlockState storeBlock = getStoredBlock(heldItemStack);
            if (storeBlock.getBlock() != Blocks.AIR)
            {
                return tryToPlaceBlock(new BlockItemUseContext(ctxt));
            }
            else
            {
                return tryToPickupBlock(player, worldIn, ctxt.getPos(), hand, ctxt.getFace(), ctxt.getHitX(), ctxt.getHitY(), ctxt.getHitZ());
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
                if (state != null)
                {
                    //Copy tile data
                    NBTTagCompound nbtTagCompound = new NBTTagCompound();
                    tileEntity.write(nbtTagCompound);

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
                        tileBox.setStateForPlacement(state);
                        tileBox.setDataForPlacement(nbtTagCompound);

                        //Consume item
                        player.getHeldItem(hand).shrink(1);

                        //Done
                        return EnumActionResult.SUCCESS;
                    }
                }
                else
                {
                    player.sendStatusMessage(new TextComponentTranslation(getTranslationKey() + ".noItem"), true);
                }
            }
            else
            {
                player.sendStatusMessage(new TextComponentTranslation(getTranslationKey() + ".noData"), true);
            }
        }
        else if (result == CanPickUpResult.BANNED_TILE)
        {
            player.sendStatusMessage(new TextComponentTranslation(getTranslationKey() + ".banned.tile"), true);
        }
        else if (result == CanPickUpResult.BANNED_BLOCK)
        {
            player.sendStatusMessage(new TextComponentTranslation(getTranslationKey() + ".banned.block"), true);
        }
        else
        {
            player.sendStatusMessage(new TextComponentTranslation(getTranslationKey() + ".noData"), true);
        }
        return EnumActionResult.SUCCESS;
    }

    protected EnumActionResult tryToPlaceBlock(BlockItemUseContext ctxt)
    {
        BlockPos pos = ctxt.getPos();
        EnumHand hand = ctxt.getPlayer().getActiveHand();
        //Move up one if not replaceable
        float hitX = ctxt.getHitX(), hitY = ctxt.getHitY(), hitZ = ctxt.getHitZ();
        if (!ctxt.canPlace())
        {
            pos = pos.offset(ctxt.getFace());
        }

        final ItemStack heldItemStack = ctxt.getItem();
        final IBlockState storedBlockState = getStoredBlock(heldItemStack);
        final NBTTagCompound savedTileData = getStoredTileData(heldItemStack);
        //Check if we can place the given block
        if (storedBlockState != null && ctxt.getPlayer().canPlayerEdit(pos, ctxt.getFace(), heldItemStack) && ctxt.getWorld().getBlockState(pos).getMaterial().isReplaceable())
        {
            Handler handler = HandlerManager.INSTANCE.getHandler(storedBlockState.getBlock());
            IBlockState blockstate = storedBlockState.getBlock().getStateForPlacement(ctxt);
            //Allow handler to control placement
            if (handler != null && handler.placeBlock(ctxt.getPlayer(), ctxt.getWorld(), pos, hand, ctxt.getFace(), hitX, hitY, hitZ, storedBlockState, savedTileData)
                    //Run normal placement if we don't have a handler or it didn't do anything
                    || placeBlock(new BlockItemUseContext(ctxt.getWorld(), ctxt.getPlayer(), heldItemStack, pos, ctxt.getFace(), hitX, hitY, hitZ), blockstate))
            	
            {
                //Get placed block
                blockstate = ctxt.getWorld().getBlockState(pos);

                //Allow handle to do post placement modification (e.g. fix rotation)
                if (handler != null)
                {
                    handler.postPlaceBlock(ctxt.getPlayer(), ctxt.getWorld(), pos, hand, ctxt.getFace(), hitX, hitY, hitZ, storedBlockState, savedTileData);
                }

                //Set tile entity data
                if (savedTileData != null)
                {
                    TileEntity tileEntity = ctxt.getWorld().getTileEntity(pos);
                    if (tileEntity != null)
                    {
                        if (handler != null)
                        {
                            handler.loadData(tileEntity, savedTileData);
                        }
                        else
                        {
                            tileEntity.read(savedTileData);
                        }
                        tileEntity.setPos(pos);
                    }
                }


                //Place audio
                SoundType soundtype = blockstate.getBlock().getSoundType(blockstate, ctxt.getWorld(), pos, ctxt.getPlayer());
                ctxt.getWorld().playSound(ctxt.getPlayer(), pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);

                //Consume item
                heldItemStack.shrink(1);

                //Return empty box
                if (!ctxt.getPlayer().isCreative() && !ctxt.getPlayer().inventory.addItemStackToInventory(new ItemStack(Cardboardboxes.blockBox)))
                {
                    ctxt.getPlayer().entityDropItem(new ItemStack(Cardboardboxes.blockBox), 0F);
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
        return stack.hasTag() ? 1 : 64;
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
        if (stack.getTag() != null && stack.getTag().hasKey(STORE_ITEM_TAG))
        {
            IBlockState state = Block.getStateById(stack.getTag().getInt(STORE_ITEM_TAG));
            {
                tooltip.add(new TextComponentTranslation(state.getBlock().getTranslationKey()));
            }
        }
    }

    public IBlockState getStoredBlock(ItemStack stack)
    {
        return stack.getTag() != null && stack.getTag().hasKey(STORE_ITEM_TAG) ? Block.getStateById(stack.getTag().getInt(STORE_ITEM_TAG)) : Blocks.AIR.getDefaultState();
    }

    public NBTTagCompound getStoredTileData(ItemStack stack)
    {
        return stack.getTag() != null && stack.getTag().hasKey(TILE_DATA_TAG) ? stack.getTag().getCompound(TILE_DATA_TAG) : null;
    }
}