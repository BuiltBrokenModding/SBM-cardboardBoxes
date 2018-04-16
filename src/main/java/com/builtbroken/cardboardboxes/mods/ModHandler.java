package com.builtbroken.cardboardboxes.mods;

import com.builtbroken.cardboardboxes.Cardboardboxes;
import com.builtbroken.cardboardboxes.handler.HandlerManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.RegistryNamespaced;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static com.builtbroken.cardboardboxes.Cardboardboxes.LOGGER;

/**
 * Created by Dark on 7/28/2015.
 */
public class ModHandler
{
    public static HashMap<String, Class<? extends ModHandler>> modSupportHandlerMap = new HashMap();

    protected static RegistryNamespaced<ResourceLocation, Class<? extends TileEntity>> TILE_REGISTRY;

    /**
     * Called in the post init phase to handle any blocks that need to be blacklisted
     */
    public void handleBlackListedContent(Configuration configuration)
    {

    }

    protected void banTileNames(ResourceLocation... names)
    {
        for (ResourceLocation name : names)
        {
            if (TILE_REGISTRY.containsKey(name))
            {
                HandlerManager.INSTANCE.banTile(TILE_REGISTRY.getObject(name));
            }
            else
            {
                Cardboardboxes.LOGGER.error("\tFailed to locate tile by name " + name + ". This is most likely a mod version issue report this error to mod author so it can be updated");
            }
        }
    }

    /**
     * Called to load and process handlers
     *
     * @param configuration
     */
    public static void loadHandlerData(Configuration configuration)
    {
        try
        {
            Field field = TileEntity.class.getDeclaredField("REGISTRY");
            field.setAccessible(true);
            TILE_REGISTRY = (RegistryNamespaced) field.get(null);

            for (Map.Entry<String, Class<? extends ModHandler>> entry : ModHandler.modSupportHandlerMap.entrySet())
            {
                if (Loader.isModLoaded(entry.getKey()))
                {
                    try
                    {
                        entry.getValue().newInstance().handleBlackListedContent(configuration);
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

            configuration.setCategoryComment("BlackListTilesByName", "Auto generated list of tiles registered in Minecraft that can be blacklisted. " +
                    "If a tile does not show up on this list it is already black listed. The reasoning behind blacklisting tiles is to prevent crashes or unwanted " +
                    "interaction. Such as picking up a piston which can both causes issues and doesn't really matter.");

            for (Class<? extends TileEntity> clazz : TILE_REGISTRY)
            {
                final ResourceLocation name = TILE_REGISTRY.getNameForObject(clazz);
                if (name != null && clazz != null)
                {
                    try
                    {
                        String clazzName = clazz.getSimpleName();
                        boolean shouldBan = HandlerManager.INSTANCE.tileEntityBanList.contains(clazz) || clazzName.contains("cable") || clazzName.contains("wire") || clazzName.contains("pipe") || clazzName.contains("tube") || clazzName.contains("conduit") || clazzName.contains("channel");
                        if (configuration.getBoolean("" + name, "BlackListTilesByName", shouldBan, "Prevents the cardboard box from picking up this tile[" + clazzName + "]"))
                        {
                            HandlerManager.INSTANCE.banTile(clazz);
                        }
                        else if (shouldBan && HandlerManager.INSTANCE.tileEntityBanList.contains(clazz))
                        {
                            //If original was banned but someone unbanned it in the config
                            HandlerManager.INSTANCE.tileEntityBanList.remove(clazz);
                        }
                    }
                    catch (Exception e)
                    {
                        LOGGER.error("Failed to add entry to config [" + name + " > " + clazz + "]", e);
                    }
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
    }
}
