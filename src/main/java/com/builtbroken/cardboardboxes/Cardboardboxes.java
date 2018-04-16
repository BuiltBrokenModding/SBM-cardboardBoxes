package com.builtbroken.cardboardboxes;

import com.builtbroken.cardboardboxes.box.BlockBox;
import com.builtbroken.cardboardboxes.box.ItemBlockBox;
import com.builtbroken.cardboardboxes.box.TileEntityBox;
import com.builtbroken.cardboardboxes.handler.HandlerManager;
import com.builtbroken.cardboardboxes.mods.ModHandler;
import com.builtbroken.cardboardboxes.mods.VanillaHandler;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

/**
 * Main mod class, handles registering content and triggering loading of interaction
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/25/2015.
 */
@Mod.EventBusSubscriber
@Mod(modid = Cardboardboxes.DOMAIN, name = "[SBM] Cardboard Boxes", version = "@MAJOR@.@MINOR@.@REVIS@.@BUILD@")
public class Cardboardboxes
{
    public static final String DOMAIN = "cardboardboxes";
    public static final String PREFIX = DOMAIN + ":";

    public static Logger LOGGER = LogManager.getLogger(DOMAIN);

    public static BlockBox blockBox;

    private static Configuration config;

    @SubscribeEvent
    public static void registerBlock(RegistryEvent.Register<Block> event)
    {
        event.getRegistry().register(blockBox = new BlockBox());
        GameRegistry.registerTileEntity(TileEntityBox.class, PREFIX + "box");
    }

    @SubscribeEvent
    public static void registerItem(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().register(new ItemBlockBox(blockBox));
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        config = new Configuration(new File(event.getModConfigurationDirectory(), "bbm/Cardboard_Boxes.cfg"));
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        //Ban our own tile :P
        HandlerManager.INSTANCE.banBlock(blockBox);
        HandlerManager.INSTANCE.banTile(TileEntityBox.class);

        //Load and run mod support
        ModHandler.modSupportHandlerMap.put("minecraft", VanillaHandler.class);
        //ModHandler.modSupportHandlerMap.put("TConstruct", TinkersConstructHandler.class); //TODO add
        //ModHandler.modSupportHandlerMap.put("BuildCraft|Factory", BuildCraftFactoryHandler.class); //TODO add
        //ModHandler.modSupportHandlerMap.put("BuildCraft|Energy", BuildCraftEnergyHandler.class); //TODO add
        //ModHandler.modSupportHandlerMap.put("BuildCraft|Transport", BuildCraftTransportHandler.class); //TODO add
        //ModHandler.modSupportHandlerMap.put("BuildCraft|Builders", BuildCraftTransportHandler.class); //TODO add
        //ModHandler.modSupportHandlerMap.put("IC2", IC2Handler.class); //TODO add
        //ModHandler.modSupportHandlerMap.put("appliedenergistics2", ModHandler.class); //TODO add

        //Trigger loading
        config.load();
        ModHandler.loadHandlerData(config);
        config.save();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {

    }
}
