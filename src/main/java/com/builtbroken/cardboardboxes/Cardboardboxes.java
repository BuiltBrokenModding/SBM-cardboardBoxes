package com.builtbroken.cardboardboxes;

import java.io.File;
import java.util.List;
import java.util.Map;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.builtbroken.cardboardboxes.box.BlockBox;
import com.builtbroken.cardboardboxes.box.ItemBlockBox;
import com.builtbroken.cardboardboxes.box.TileEntityBox;
import com.builtbroken.cardboardboxes.handler.HandlerManager;
import com.builtbroken.cardboardboxes.mods.ModHandler;
import com.builtbroken.cardboardboxes.mods.VanillaHandler;

import io.netty.handler.codec.http2.Http2FrameReader.Configuration;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Main mod class, handles registering content and triggering loading of interaction
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/25/2015.
 */
@Mod.EventBusSubscriber(bus = Bus.MOD)
@Mod(Cardboardboxes.DOMAIN)
public class Cardboardboxes {
    public static final String DOMAIN = "cardboardboxes";
    public static final String PREFIX = DOMAIN + ":";

    public static Logger LOGGER = LogManager.getLogger();

    public static BlockBox blockBox;
    public static TileEntityType<TileEntityBox> tileBox;

    private static ForgeConfigSpec config;

    public Cardboardboxes() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        ModHandler.modSupportHandlerMap.put("minecraft", VanillaHandler.class);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, config = ModHandler.buildHandlerData());
        LOGGER.info("Finished building the config -> " + config);
    }

    private void setup(final FMLCommonSetupEvent e) {
        HandlerManager.INSTANCE.banBlock(blockBox);
        HandlerManager.INSTANCE.banTile(tileBox);

        ModHandler.loadHandlerData(config);
    }

    @SubscribeEvent
    public static void registerBlock(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(blockBox = new BlockBox());
    }

    @SubscribeEvent
    public static void registerItem(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new ItemBlockBox(blockBox));
    }

    @SubscribeEvent
    public static void registerTileEntity(RegistryEvent.Register<TileEntityType<?>> event) {
        tileBox = TileEntityType.register(PREFIX + "box", TileEntityType.Builder.create(TileEntityBox::new));
    }
}
