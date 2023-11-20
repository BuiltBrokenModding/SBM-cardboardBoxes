package com.builtbroken.cardboardboxes;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.builtbroken.cardboardboxes.box.BoxBlock;
import com.builtbroken.cardboardboxes.box.BoxBlockEntity;
import com.builtbroken.cardboardboxes.box.BoxBlockItem;
import com.builtbroken.cardboardboxes.handler.HandlerManager;
import com.builtbroken.cardboardboxes.mods.ModHandler;
import com.builtbroken.cardboardboxes.mods.VanillaHandler;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Main mod class, handles registering content and triggering loading of interaction
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/25/2015.
 */
@Mod(Cardboardboxes.DOMAIN)
public class Cardboardboxes {
    public static final String DOMAIN = "cardboardboxes";
    public static final Logger LOGGER = LogManager.getLogger();

    // Blocks
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(DOMAIN);
    public static final DeferredBlock<BoxBlock> BOX_BLOCK = BLOCKS.register("cardboardbox", () -> new BoxBlock(null));
    public static final List<DeferredBlock<BoxBlock>> BOX_COLORS = Arrays.stream(TabSortedColors.values()).map(TabSortedColors::toDyeColor).map(color ->
    BLOCKS.register("box_" + color.getName(), () -> new BoxBlock(color))).toList();

    // Tiles
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, DOMAIN);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BoxBlockEntity>> BOX_BLOCK_ENTITY_TYPE = BLOCK_ENTITY_TYPES.register("box", () -> BlockEntityType.Builder.of(BoxBlockEntity::new, BOX_BLOCK.get()).build(null));

    // Items
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(DOMAIN);
    public static final DeferredItem<BoxBlockItem> BOX_ITEM = ITEMS.register("cardboardbox", () -> new BoxBlockItem(BOX_BLOCK.get(), null));
    public static final List<DeferredItem<BoxBlockItem>> BOX_ITEM_COLORS = BOX_COLORS.stream().map(defBlock ->
    ITEMS.register(defBlock.getId().getPath(), () -> new BoxBlockItem(defBlock.get(), defBlock.get().color))).toList();

    // Config
    private static ModConfigSpec config;

    public Cardboardboxes(IEventBus modBus) {
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

    private void onCreativeModeTabBuildContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS || event.getTabKey() == CreativeModeTabs.COLORED_BLOCKS) {
            event.accept(BOX_ITEM.get());
            BOX_COLORS.forEach((defBlock) -> event.accept(defBlock.get()));
        }
    }

    private static enum TabSortedColors {
        WHITE(0),
        LIGHT_GRAY(8),
        GRAY(7),
        BLACK(15),
        BROWN(12),
        RED(14),
        ORANGE(1),
        YELLOW(4),
        LIME(5),
        GREEN(13),
        CYAN(9),
        LIGHT_BLUE(3),
        BLUE(11),
        PURPLE(10),
        MAGENTA(2),
        PINK(6);

        private int dyeColorId;

        private TabSortedColors(int dyeColorId) {
            this.dyeColorId = dyeColorId;
        }

        public DyeColor toDyeColor() {
            return DyeColor.byId(dyeColorId);
        }
    }
}
