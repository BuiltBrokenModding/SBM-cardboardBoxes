package com.builtbroken.cardboardboxes;

import com.builtbroken.cardboardboxes.box.BlockBox;
import com.builtbroken.cardboardboxes.box.ItemBlockBox;
import com.builtbroken.cardboardboxes.box.TileBox;
import com.builtbroken.cardboardboxes.handler.HandlerManager;
import com.builtbroken.cardboardboxes.mods.MainModHandler;
import com.builtbroken.cardboardboxes.mods.ModSupportHandler;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.HashMap;

/**
 * Created by Dark on 7/25/2015.
 */
@Mod.EventBusSubscriber
@Mod(modid = Cardboardboxes.DOMAIN, name = "Cardboard Boxes", version = "@MAJOR@.@MINOR@.@REVIS@.@BUILD@")
public class Cardboardboxes {
    public static final String DOMAIN = "cardboardboxes";
    public static final String PREFIX = DOMAIN + ":";

    @SidedProxy(clientSide = "com.builtbroken.cardboardboxes.CommonProxy", serverSide = "com.builtbroken.cardboardboxes.CommonProxy")
    public static CommonProxy proxy;

    public static Configuration config;
    public static Logger LOGGER;

    public static BlockBox blockBox = new BlockBox();
    public static ItemBlockBox itemBlockBox = new ItemBlockBox(blockBox);

    public static HandlerManager boxHandler;

    public static HashMap<String, Class<? extends ModSupportHandler>> modSupportHandlerMap = new HashMap();

    @SubscribeEvent
    public static void registerBlock(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(blockBox);
        GameRegistry.registerTileEntity(TileBox.class, "cbcardboardbox");
    }

    @SubscribeEvent
    public static void registerItem(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(itemBlockBox);
    }

    @SubscribeEvent
    public static void registerModel(ModelRegistryEvent event) {
        blockBox.registerModel();
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOGGER = LogManager.getLogger("CardboardBoxes");
        boxHandler = new HandlerManager();
        config = new Configuration(new File(event.getModConfigurationDirectory(), "bbm/Cardboard_Boxes.cfg"));
        config.load();
        proxy.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        MainModHandler.banDefaultTiles();
        proxy.postInit();
        config.save();
    }
}
