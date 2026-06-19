package com.builtbroken.cardboardboxes;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

import com.builtbroken.cardboardboxes.box.BoxBlock;
import com.builtbroken.cardboardboxes.box.BoxBlockEntity;
import com.builtbroken.cardboardboxes.box.BoxBlockItem;
import com.builtbroken.cardboardboxes.handler.HandlerManager;
import com.builtbroken.cardboardboxes.mods.ModHandler;
import com.builtbroken.cardboardboxes.mods.VanillaHandler;
import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.Registries;
import net.minecraft.references.BlockItemId;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.ColorCollection;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
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
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final ColorCollection<BlockItemId> IDS = ColorCollection.NAMES.map(color -> {
        Identifier base = Identifier.fromNamespaceAndPath(DOMAIN, "box_" + color);
        return BlockItemId.create(base, base);
    });

    // Blocks
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(DOMAIN);
    public static final DeferredBlock<BoxBlock> BOX_BLOCK = BLOCKS.registerBlock("cardboardbox", p -> new BoxBlock(null, p));
    public static final ColorCollection<DeferredBlock<BoxBlock>> BOX_COLORS = ColorCollection.zipMap(IDS, ColorCollection.VALUES, (id, color) ->
        BLOCKS.registerBlock(
            id.block().identifier().getPath(),
            p -> new BoxBlock(color, p)
        )
    );

    // Tiles
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, DOMAIN);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BoxBlockEntity>> BOX_BLOCK_ENTITY_TYPE = BLOCK_ENTITY_TYPES.register("box", () -> {
        List<BoxBlock> boxList = new ArrayList<>(BOX_COLORS.map(DeferredBlock::get).asList());

        boxList.addFirst(BOX_BLOCK.get());
        return new BlockEntityType<>(BoxBlockEntity::new, boxList.toArray(new BoxBlock[0]));
    });

    // Items
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(DOMAIN);
    public static final DeferredItem<BoxBlockItem> BOX_ITEM = ITEMS.registerItem("cardboardbox", p -> new BoxBlockItem(BOX_BLOCK.get(), null, p), () -> new Item.Properties().useBlockDescriptionPrefix());
    public static final ColorCollection<DeferredItem<BoxBlockItem>> BOX_ITEM_COLORS = ColorCollection.zipMap(IDS, ColorCollection.VALUES, (id, color) ->
        ITEMS.registerItem(
            id.item().identifier().getPath(),
            p -> new BoxBlockItem(BOX_COLORS.pick(color).get(), color, p),
            () -> new Item.Properties().useBlockDescriptionPrefix()
        )
    );

    // Config
    private static ModConfigSpec config;

    public Cardboardboxes(IEventBus modBus, ModContainer modContainer) {
        modBus.addListener(this::setup);
        modBus.addListener(this::onCreativeModeTabBuildContents);
        ModHandler.modSupportHandlerMap.put("minecraft", VanillaHandler.class);
        modContainer.registerConfig(ModConfig.Type.COMMON, config = ModHandler.buildHandlerData());
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
        List<DyeColor> gameplayColorOrder = List.of(
            DyeColor.WHITE,
            DyeColor.LIGHT_GRAY,
            DyeColor.GRAY,
            DyeColor.BLACK,
            DyeColor.BROWN,
            DyeColor.RED,
            DyeColor.ORANGE,
            DyeColor.YELLOW,
            DyeColor.LIME,
            DyeColor.GREEN,
            DyeColor.CYAN,
            DyeColor.LIGHT_BLUE,
            DyeColor.BLUE,
            DyeColor.PURPLE,
            DyeColor.MAGENTA,
            DyeColor.PINK
        );

        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS || event.getTabKey() == CreativeModeTabs.COLORED_BLOCKS) {
            event.accept(BOX_ITEM.get());
            gameplayColorOrder.forEach(dye -> event.accept(BOX_COLORS.pick(dye)));
        }
    }
}
