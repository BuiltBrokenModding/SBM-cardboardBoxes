package com.builtbroken.cardboardboxes;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Blocks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.builtbroken.cardboardboxes.box.BoxBlock;
import com.builtbroken.cardboardboxes.box.BoxBlockEntity;
import com.builtbroken.cardboardboxes.box.BoxBlockItem;
import com.builtbroken.cardboardboxes.handler.HandlerManager;
import com.builtbroken.cardboardboxes.mods.ModHandler;
import com.builtbroken.cardboardboxes.mods.VanillaHandler;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Arrays;
import java.util.List;

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
	public static final Logger LOGGER = LogManager.getLogger();

	// Blocks
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, DOMAIN);
	public static final RegistryObject<BoxBlock> BOX_BLOCK = BLOCKS.register("cardboardbox", () -> new BoxBlock(null));
	public static final List<RegistryObject<BoxBlock>> BOX_COLORS = Arrays.stream(DyeColor.values()).map(color ->
			BLOCKS.register("box_" + color.getName(), () -> new BoxBlock(color))).toList();

	// Tiles
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, DOMAIN);
	public static final RegistryObject<BlockEntityType<BoxBlockEntity>> BOX_BLOCK_ENTITY_TYPE = BLOCK_ENTITY_TYPES.register("box", () -> BlockEntityType.Builder.of(BoxBlockEntity::new, BOX_BLOCK.get()).build(null));

	// Items
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, DOMAIN);
	public static final RegistryObject<BoxBlockItem> BOX_ITEM = ITEMS.register("cardboardbox", () -> new BoxBlockItem(BOX_BLOCK.get(), null));
	public static final List<RegistryObject<BoxBlockItem>> BOX_ITEM_COLORS = BOX_COLORS.stream().map(defBlock ->
			ITEMS.register(defBlock.getId().getPath(), () -> new BoxBlockItem(defBlock.get(), defBlock.get().color))).toList();

	// Config
	private static ForgeConfigSpec config;

	public Cardboardboxes() {
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

		modBus.addListener(this::setup);
		modBus.addListener(this::onCreativeModeTabBuildContents);
		ModHandler.modSupportHandlerMap.put("minecraft", VanillaHandler.class);
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, config = ModHandler.buildHandlerData());
		LOGGER.info("Finished building the config -> " + config);

		BLOCKS.register(modBus);
		BLOCK_ENTITY_TYPES.register(modBus);
		ITEMS.register(modBus);
	}

	private void setup(final FMLCommonSetupEvent e) {
		HandlerManager.INSTANCE.banBlock(BOX_BLOCK.get());
		BOX_COLORS.forEach((defBlock) -> HandlerManager.INSTANCE.banBlock(defBlock.get()));
		HandlerManager.INSTANCE.banBlockEntity(BOX_BLOCK_ENTITY_TYPE.get());

		ModHandler.loadHandlerData(config);
	}

	private void onCreativeModeTabBuildContents(CreativeModeTabEvent.BuildContents event) {
		if (event.getTab() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
			event.accept(BOX_ITEM.get());
			BOX_COLORS.forEach((defBlock) -> event.accept(defBlock.get()));
		}
	}
}
