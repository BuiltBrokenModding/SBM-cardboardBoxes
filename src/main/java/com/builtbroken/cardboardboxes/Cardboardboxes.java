package com.builtbroken.cardboardboxes;

import com.builtbroken.cardboardboxes.box.BlockBox;
import com.builtbroken.cardboardboxes.box.ItemBlockBox;
import com.builtbroken.cardboardboxes.box.TileBox;
import com.builtbroken.cardboardboxes.handler.HandlerManager;
import com.builtbroken.cardboardboxes.mods.IC2Handler;
import com.builtbroken.cardboardboxes.mods.ModSupportHandler;
import com.builtbroken.cardboardboxes.mods.TinkersConstructHandler;
import com.builtbroken.cardboardboxes.mods.buildcraft.BuildCraftEnergyHandler;
import com.builtbroken.cardboardboxes.mods.buildcraft.BuildCraftFactoryHandler;
import com.builtbroken.cardboardboxes.mods.buildcraft.BuildCraftTransportHandler;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.*;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dark on 7/25/2015.
 */
@Mod(modid = Cardboardboxes.DOMAIN, name = "Cardboard Boxes", version = "@MAJOR@.@MINOR@.@REVIS@.@BUILD@")
public class Cardboardboxes
{
    public static final String DOMAIN = "cardboardboxes";
    public static final String PREFIX = DOMAIN + ":";

    @SidedProxy(clientSide = "com.builtbroken.cardboardboxes.CommonProxy", serverSide = "com.builtbroken.cardboardboxes.CommonProxy")
    public static CommonProxy proxy;

    public static Configuration config;
    public static Logger LOGGER;

    public static BlockBox blockBox;

    public static HandlerManager boxHandler;

    public static HashMap<String, Class<? extends ModSupportHandler>> modSupportHandlerMap = new HashMap();

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        LOGGER = LogManager.getLogger("CardboardBoxes");
        boxHandler = new HandlerManager();
        config = new Configuration(new File(event.getModConfigurationDirectory(), "bbm/Cardboard_Boxes.cfg"));
        config.load();

        //Create block
        blockBox = new BlockBox();
        GameRegistry.register(blockBox);
        GameRegistry.register(new ItemBlockBox(blockBox), blockBox.getRegistryName());
        if (event.getSide() == Side.CLIENT)
        {
            blockBox.registerModel();
        }
        GameRegistry.registerTileEntity(TileBox.class, "cbCardboardBox");
        proxy.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        if (config.getBoolean("BlackListMobSpawners", "BlackListSettings", true, "Prevents mobs spawners from being placed into cardboard boxes"))
        {
            boxHandler.banBlock(Blocks.MOB_SPAWNER);
            boxHandler.banTile(TileEntityMobSpawner.class);
        }

        //Remove unwanted interaction
        boxHandler.banBlock(Blocks.BEACON);
        boxHandler.banTile(TileEntityBeacon.class);
        boxHandler.banBlock(Blocks.PISTON);
        boxHandler.banBlock(Blocks.PISTON_EXTENSION);
        boxHandler.banBlock(Blocks.PISTON_HEAD);
        boxHandler.banBlock(Blocks.STICKY_PISTON);
        boxHandler.banTile(TileEntityPiston.class);
        boxHandler.banBlock(Blocks.DAYLIGHT_DETECTOR);
        boxHandler.banTile(TileEntityDaylightDetector.class);
        boxHandler.banBlock(Blocks.ENDER_CHEST);
        boxHandler.banTile(TileEntityEnderChest.class);
        boxHandler.banBlock(Blocks.POWERED_COMPARATOR);
        boxHandler.banBlock(Blocks.UNPOWERED_COMPARATOR);
        boxHandler.banTile(TileEntityComparator.class);
        boxHandler.banBlock(Blocks.COMMAND_BLOCK);
        boxHandler.banTile(TileEntityCommandBlock.class);
        boxHandler.banBlock(Blocks.END_PORTAL);
        boxHandler.banBlock(Blocks.END_PORTAL_FRAME);
        boxHandler.banTile(TileEntityEndPortal.class);
        boxHandler.banBlock(Blocks.NOTEBLOCK);
        boxHandler.banTile(TileEntityNote.class);
        boxHandler.banBlock(Blocks.ENCHANTING_TABLE);
        boxHandler.banTile(TileEntityEnchantmentTable.class);
        boxHandler.banBlock(Blocks.STANDING_SIGN);
        boxHandler.banBlock(Blocks.WALL_SIGN);
        boxHandler.banTile(TileEntitySign.class);
        boxHandler.banBlock(Blocks.SKULL);
        boxHandler.banTile(TileEntitySkull.class);
        boxHandler.banBlock(Blocks.CAULDRON);
        boxHandler.banBlock(Blocks.FLOWER_POT);
        boxHandler.banTile(TileEntityFlowerPot.class);

        //Ban our own tile :P
        boxHandler.banBlock(blockBox);
        boxHandler.banTile(TileBox.class);

        //Load and run mod support
        modSupportHandlerMap.put("TConstruct", TinkersConstructHandler.class);
        modSupportHandlerMap.put("BuildCraft|Factory", BuildCraftFactoryHandler.class);
        modSupportHandlerMap.put("BuildCraft|Energy", BuildCraftEnergyHandler.class);
        modSupportHandlerMap.put("BuildCraft|Transport", BuildCraftTransportHandler.class);
        modSupportHandlerMap.put("BuildCraft|Builders", BuildCraftTransportHandler.class);
        modSupportHandlerMap.put("IC2", IC2Handler.class);
        modSupportHandlerMap.put("appliedenergistics2", ModSupportHandler.class);
        try
        {
            Field field;
            try
            {
                field = TileEntity.class.getDeclaredField("field_145855_i");
            } catch (NoSuchFieldException e)
            {
                field = TileEntity.class.getDeclaredField("nameToClassMap");
            }
            field.setAccessible(true);
            Map<String, Class> map = (Map) field.get(null);
            for (Map.Entry<String, Class<? extends ModSupportHandler>> entry : modSupportHandlerMap.entrySet())
            {
                if (Loader.isModLoaded(entry.getKey()))
                {
                    try
                    {
                        entry.getValue().newInstance().handleBlackListedContent(map);
                    } catch (InstantiationException e)
                    {
                        LOGGER.error("Failed to create handler for mod " + entry.getKey());
                        e.printStackTrace();
                    } catch (IllegalAccessException e)
                    {
                        LOGGER.error("Failed to access constructor for handler for mod " + entry.getKey());
                        e.printStackTrace();
                    }
                }
            }

            //TODO see if we can sort the files by mod to help users find what they are looking for
            for (Map.Entry<String, Class> entry : map.entrySet())
            {
                try
                {
                    config.setCategoryComment("BlackListTilesByName", "Auto generated list of tiles registered in Minecraft that can be blacklisted. If a tile does not show up on this list it is already black listed. The reasoning behind blacklisting tiles is to prevent crashes or unwanted interaction. Such as picking up a piston which can both causes issues and doesn't really matter.");
                    Class<? extends TileEntity> clazz = entry.getValue();
                    String name = entry.getKey();
                    if (name != null && !name.isEmpty())
                    {
                        if (clazz != null)
                        {
                            String clazzName = clazz.getSimpleName();
                            boolean shouldBan = boxHandler.blackListedTiles.contains(clazz) || clazzName.contains("cable") || clazzName.contains("wire") || clazzName.contains("pipe") || clazzName.contains("tube") || clazzName.contains("conduit") || clazzName.contains("channel");
                            if (config.getBoolean("" + clazz, "BlackListTilesByName", shouldBan, "Prevents the cardboard box from picking up this tile[" + name + "]"))
                            {
                                boxHandler.banTile(clazz);
                            } else if (shouldBan && boxHandler.blackListedTiles.contains(clazz))
                            {
                                //If original was banned but someone unbanned it in the config
                                boxHandler.blackListedTiles.remove(clazz);
                            }
                        }
                    }
                } catch (Exception e)
                {
                    LOGGER.error("Failed to add entry to config " + entry);
                    e.printStackTrace();
                }

            }
        } catch (NoSuchFieldException e)
        {
            LOGGER.error("Failed to find the tile map field");
            e.printStackTrace();
        } catch (IllegalAccessException e)
        {
            LOGGER.error("Failed to access tile map");
            e.printStackTrace();
        } catch (Exception e)
        {
            LOGGER.error("Failed to add tile map to config");
            e.printStackTrace();
        }


        config.save();

        proxy.postInit();
    }
}
