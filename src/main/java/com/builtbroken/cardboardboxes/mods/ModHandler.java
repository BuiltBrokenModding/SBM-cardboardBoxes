package com.builtbroken.cardboardboxes.mods;

import static com.builtbroken.cardboardboxes.Cardboardboxes.LOGGER;

import java.util.HashMap;
import java.util.Map;

import com.builtbroken.cardboardboxes.handler.HandlerManager;

import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * Prefab for handling interaction for a mod or content package
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/28/2015.
 */
public class ModHandler {
    public static HashMap<String, Class<? extends ModHandler>> modSupportHandlerMap = new HashMap<>();
    public static HashMap<String, ModHandler> modSupportHandlerMap_instances = new HashMap<>();

    protected static IForgeRegistry<TileEntityType<?>> TILE_REGISTRY;
    public static HashMap<String, ForgeConfigSpec.BooleanValue> tileBanConfigMap = new HashMap<>();

    public void load(ForgeConfigSpec configuration) {
    }

    public void build(ForgeConfigSpec.Builder b) {
    }

    /**
     * Called to build config
     */
    public static ForgeConfigSpec buildHandlerData() {
        //Create the builder
        ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();
        LOGGER.info("ModHandler#buildHandlerData() -> Loading data and data handlers");
        loadTileRegistry();
        buildHandlers(b);
        buildConfig(b);
        return b.build();
    }

    /**
     * Called to process handlers
     *
     * @param configuration
     */
    public static void loadHandlerData(ForgeConfigSpec configuration) {
        LOGGER.info("ModHandler#loadHandlerData() -> Accessed Tile Registry: " + (TILE_REGISTRY != null));
        processHandlers(configuration);
        LOGGER.info("ModHandler#loadHandlerData() -> Finished loading data handlers");
        loadConfig(configuration);
        LOGGER.info("ModHandler#loadHandlerData() -> Finished loading configurations");
    }

    private static void buildHandlers(ForgeConfigSpec.Builder b) {
        for (Map.Entry<String, Class<? extends ModHandler>> entry : ModHandler.modSupportHandlerMap.entrySet()) {
            if (ModList.get().isLoaded(entry.getKey()) || entry.getKey().equals("minecraft")) {
                try {
                    ModHandler instance = entry.getValue().newInstance();
                    modSupportHandlerMap_instances.put(entry.getKey(), instance);
                    instance.build(b);
                } catch (InstantiationException e) {
                    LOGGER.error("Failed to create handler for mod " + entry.getKey());
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    LOGGER.error("Failed to access constructor for handler for mod " + entry.getKey());
                    e.printStackTrace();
                }
            }
        }
    }

    private static void processHandlers(ForgeConfigSpec configuration) {
        for (Map.Entry<String, ModHandler> entry : ModHandler.modSupportHandlerMap_instances.entrySet()) {
            entry.getValue().load(configuration);
        }
    }

    public static void buildConfig(ForgeConfigSpec.Builder b) {
        String comment = "Auto generated list of tiles registered in Minecraft that can be blocked from use with the box. " +
                "If a tile does not show up on this list it is already black listed. The reasoning behind blocking tiles is to prevent crashes or unwanted " +
                "interaction. Such as picking up a piston which can both causes issues and doesn't really matter. Set value to 'true' to disable interaction.";
        b.comment(comment).push("tile_ban_list"); //set the category
        for (ResourceLocation name : TILE_REGISTRY.getKeys()) {
            TileEntityType<?> type = TILE_REGISTRY.getValue(name);
            if (name != null && type != null) {
                try {
                    String typeString = type.getRegistryName().toString();
                    boolean shouldBan = HandlerManager.tileEntityBanList.contains(type) || typeString.contains("cable") || typeString.contains("wire") || typeString.contains("pipe") || typeString.contains("tube") || typeString.contains("conduit") || typeString.contains("channel");
                    tileBanConfigMap.put(typeString, b.define(typeString, shouldBan));
                } catch (Exception e) {
                    LOGGER.error("ModHandler#buildConfig() -> Failed to add entry to config [" + name + " > " + type + "]", e);
                }
            }
        }
        b.pop(); //go back to top level category

    }

    private static void loadConfig(ForgeConfigSpec configuration) {
        if (TILE_REGISTRY != null) {
            for (ResourceLocation name : TILE_REGISTRY.getKeys()) {
                TileEntityType<?> type = TILE_REGISTRY.getValue(name);
                if (name != null && type != null) {
                    try {
                        String typeString = type.getRegistryName().toString();
                        boolean shouldBan = HandlerManager.tileEntityBanList.contains(type) || typeString.contains("cable") || typeString.contains("wire") || typeString.contains("pipe") || typeString.contains("tube") || typeString.contains("conduit") || typeString.contains("channel");
                        if (tileBanConfigMap.containsKey(typeString) ? tileBanConfigMap.get(typeString).get() : false) {
                            HandlerManager.INSTANCE.banTile(type);
                        } else if (shouldBan) {
                            //If original was banned but someone unbanned it in the config
                            HandlerManager.tileEntityBanList.remove(type);
                        }
                    } catch (Exception e) {
                        LOGGER.error("ModHandler#loadHandlerData() -> Failed to add entry to config [" + name + " > " + type + "]", e);
                    }
                }
            }
        }
    }

    private static void loadTileRegistry() {
        TILE_REGISTRY = ForgeRegistries.TILE_ENTITIES;
    }
}
