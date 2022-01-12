package com.builtbroken.cardboardboxes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.builtbroken.cardboardboxes.box.BlockEntityBox;
import com.builtbroken.cardboardboxes.box.BoxBlock;
import com.builtbroken.cardboardboxes.box.BoxItemBlock;
import com.builtbroken.cardboardboxes.handler.HandlerManager;
import com.builtbroken.cardboardboxes.mods.ModHandler;
import com.builtbroken.cardboardboxes.mods.VanillaHandler;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.config.ModConfig;
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

    public static BoxBlock boxBlock;
    public static BlockEntityType<BlockEntityBox> tileBox;

    private static ForgeConfigSpec config;

    public Cardboardboxes() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        ModHandler.modSupportHandlerMap.put("minecraft", VanillaHandler.class);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, config = ModHandler.buildHandlerData());
        LOGGER.info("Finished building the config -> " + config);
    }

    @SubscribeEvent
    public static void registerBlock(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(boxBlock = new BoxBlock());
    }

    @SubscribeEvent
    public static void registerItem(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new BoxItemBlock(boxBlock));
    }

    @SubscribeEvent
    public static void registerTileEntity(RegistryEvent.Register<BlockEntityType<?>> event) {
        event.getRegistry().register(BlockEntityType.Builder.of(BlockEntityBox::new, boxBlock).build(null).setRegistryName(new ResourceLocation(PREFIX + "box")));
    }

    private void setup(final FMLCommonSetupEvent e) {
        HandlerManager.INSTANCE.banBlock(boxBlock);
        HandlerManager.INSTANCE.banTile(tileBox);

        ModHandler.loadHandlerData(config);
    }
}
