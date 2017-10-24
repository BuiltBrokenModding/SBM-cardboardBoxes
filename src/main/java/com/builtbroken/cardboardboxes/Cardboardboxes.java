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
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.*;
import net.minecraftforge.common.config.Configuration;
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

    public static final String blackListConfigCategory = "BlackListTilesByID";

    @SidedProxy(clientSide = "com.builtbroken.cardboardboxes.ClientProxy", serverSide = "com.builtbroken.cardboardboxes.CommonProxy")
    public static CommonProxy proxy;

    public static Configuration config;
    public static Logger LOGGER;

    public static Block blockBox;

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
        GameRegistry.registerBlock(blockBox, ItemBlockBox.class, "cbCardboardBox");
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
            boxHandler.banBlock(Blocks.mob_spawner);
            boxHandler.banTile(TileEntityMobSpawner.class);
        }

        //Remove unwanted interaction
        boxHandler.banBlock(Blocks.beacon);
        boxHandler.banTile(TileEntityBeacon.class);
        boxHandler.banBlock(Blocks.piston);
        boxHandler.banBlock(Blocks.piston_extension);
        boxHandler.banBlock(Blocks.piston_head);
        boxHandler.banBlock(Blocks.sticky_piston);
        boxHandler.banTile(TileEntityPiston.class);
        boxHandler.banBlock(Blocks.daylight_detector);
        boxHandler.banTile(TileEntityDaylightDetector.class);
        boxHandler.banBlock(Blocks.ender_chest);
        boxHandler.banTile(TileEntityEnderChest.class);
        boxHandler.banBlock(Blocks.powered_comparator);
        boxHandler.banBlock(Blocks.unpowered_comparator);
        boxHandler.banTile(TileEntityComparator.class);
        boxHandler.banBlock(Blocks.command_block);
        boxHandler.banTile(TileEntityCommandBlock.class);
        boxHandler.banBlock(Blocks.end_portal);
        boxHandler.banBlock(Blocks.end_portal_frame);
        boxHandler.banTile(TileEntityEndPortal.class);
        boxHandler.banBlock(Blocks.noteblock);
        boxHandler.banTile(TileEntityNote.class);
        boxHandler.banBlock(Blocks.enchanting_table);
        boxHandler.banTile(TileEntityEnchantmentTable.class);
        boxHandler.banBlock(Blocks.standing_sign);
        boxHandler.banBlock(Blocks.wall_sign);
        boxHandler.banTile(TileEntitySign.class);
        boxHandler.banBlock(Blocks.skull);
        boxHandler.banTile(TileEntitySkull.class);
        boxHandler.banBlock(Blocks.cauldron);
        boxHandler.banBlock(Blocks.flower_pot);
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

        //Load tiles
        try
        {
            //Get tile map field
            Field field;
            try
            {
                field = TileEntity.class.getDeclaredField("field_145855_i");
            }
            catch (NoSuchFieldException e)
            {
                field = TileEntity.class.getDeclaredField("nameToClassMap");
            }
            field.setAccessible(true);

            //Loop over tiles
            Map<String, Class> map = (Map) field.get(null);

            //Load mod support and check for blacklist
            for (Map.Entry<String, Class<? extends ModSupportHandler>> entry : modSupportHandlerMap.entrySet())
            {
                if (Loader.isModLoaded(entry.getKey()))
                {
                    try
                    {
                        entry.getValue().newInstance().handleBlackListedContent(map); //TODO rework
                    }
                    catch (InstantiationException e)
                    {
                        LOGGER.error("Failed to create handler for mod " + entry.getKey());
                        e.printStackTrace();
                    }
                    catch (IllegalAccessException e)
                    {
                        LOGGER.error("Failed to access constructor for handler for mod " + entry.getKey());
                        e.printStackTrace();
                    }
                }
            }


            //TODO see if we can sort the files by mod to help users find what they are looking for

            //Loop over tile entities adding them to the config for disabling
            for (Map.Entry<String, Class> entry : map.entrySet())
            {
                try
                {
                    config.setCategoryComment(blackListConfigCategory, "Auto generated list of tiles registered in Minecraft that can be blacklisted. " +
                            "If a tile does not show up on this list it is already black listed. The reasoning behind blacklisting tiles is to prevent " +
                            "crashes or unwanted interaction. Such as picking up a piston which can both causes issues and doesn't really matter.");

                    //Get tile id
                    final String registeredID = entry.getKey();
                    if (registeredID != null && !registeredID.isEmpty())
                    {
                        //Get tile class
                        final Class<? extends TileEntity> tileClazz = entry.getValue();
                        if (tileClazz != null)
                        {
                            //Get class as a string
                            final String clazzName = tileClazz.getName().replace("class ", "");

                            //Check if tile should be banned by default
                            boolean shouldBan = boxHandler.blackListedTiles.contains(tileClazz) || clazzName.contains("cable") || clazzName.contains("wire") || clazzName.contains("pipe") || clazzName.contains("tube") || clazzName.contains("conduit") || clazzName.contains("channel");

                            //Check config
                            if (config.getBoolean("" + registeredID, blackListConfigCategory, shouldBan, "Prevents the cardboard box from picking up this tile[" + clazzName + "]"))
                            {
                                boxHandler.banTile(tileClazz);
                            }
                            //Check blacklist
                            else if (shouldBan && boxHandler.blackListedTiles.contains(tileClazz))
                            {
                                //If original was banned but someone unbanned it in the config
                                boxHandler.blackListedTiles.remove(tileClazz);
                            }
                        }
                    }
                }
                catch (Exception e)
                {
                    LOGGER.error("Failed to add entry to config " + entry);
                    e.printStackTrace();
                }
            }
        }
        catch (NoSuchFieldException e)
        {
            LOGGER.error("Failed to find the tile map field");
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            LOGGER.error("Failed to access tile map");
            e.printStackTrace();
        }
        catch (Exception e)
        {
            LOGGER.error("Failed to add tile map to config");
            e.printStackTrace();
        }


        config.save();

        proxy.postInit();
    }
}
