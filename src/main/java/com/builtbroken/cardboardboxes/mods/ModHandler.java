package com.builtbroken.cardboardboxes.mods;

import static com.builtbroken.cardboardboxes.Cardboardboxes.LOGGER;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.builtbroken.cardboardboxes.Cardboardboxes;
import com.builtbroken.cardboardboxes.handler.HandlerManager;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.RegistryNamespaced;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;

/**
 * Prefab for handling interaction for a mod or content package
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/28/2015.
 */
public class ModHandler
{
	public static HashMap<String, Class<? extends ModHandler>> modSupportHandlerMap = new HashMap<>();

	protected static RegistryNamespaced<ResourceLocation, Class<? extends TileEntity>> TILE_REGISTRY;

	/**
	 * Called in the post init phase to handle any blocks that need to be blacklisted
	 */
	public void load(Configuration configuration)
	{

	}

	/**
	 * Helper method to ban TileEntity.class by registry name
	 *
	 * @param names - registry name of the tile
	 */
	protected void banTileNames(ResourceLocation... names)
	{
		if (TILE_REGISTRY != null)
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
		else
		{
			LOGGER.error("Attempted to ban tiles but TILE_REGISTRY was null. This could cause issues with interaction, check log above for possible cause.");
		}
	}

	/**
	 * Called to load and process handlers
	 *
	 * @param configuration
	 */
	public static void loadHandlerData(Configuration configuration)
	{
		LOGGER.info("ModHandler#loadHandlerData() -> Loading data and data handlers");
		loadTileRegistry();
		LOGGER.info("ModHandler#loadHandlerData() -> Accessed Tile Registry: " + (TILE_REGISTRY != null));
		processHandlers(configuration);
		LOGGER.info("ModHandler#loadHandlerData() -> Finished loading data handlers");
		loadConfig(configuration);
		LOGGER.info("ModHandler#loadHandlerData() -> Finished loading configurations");
	}

	private static void processHandlers(Configuration configuration)
	{
		for (Map.Entry<String, Class<? extends ModHandler>> entry : ModHandler.modSupportHandlerMap.entrySet())
		{
			if (Loader.isModLoaded(entry.getKey()))
			{
				try
				{
					entry.getValue().newInstance().load(configuration);
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
	}

	private static void loadConfig(Configuration configuration)
	{
		if (TILE_REGISTRY != null)
		{
			final String cat_name = "tile_ban_list";
			configuration.setCategoryComment(cat_name, "Auto generated list of tiles registered in Minecraft that can be blocked from use with the box. " +
					"If a tile does not show up on this list it is already black listed. The reasoning behind blocking tiles is to prevent crashes or unwanted " +
					"interaction. Such as picking up a piston which can both causes issues and doesn't really matter. Set value to 'true' to disable interaction.");

			for (ResourceLocation name : TILE_REGISTRY.getKeys())
			{
				Class<? extends TileEntity> clazz = TILE_REGISTRY.getObject(name);
				if (name != null && clazz != null)
				{
					try
					{
						String clazzName = clazz.getSimpleName();
						boolean shouldBan = HandlerManager.tileEntityBanList.contains(clazz) || clazzName.contains("cable") || clazzName.contains("wire") || clazzName.contains("pipe") || clazzName.contains("tube") || clazzName.contains("conduit") || clazzName.contains("channel");
						if (configuration.getBoolean("" + name, cat_name, shouldBan, "Clazz[" + clazzName + "]"))
						{
							HandlerManager.INSTANCE.banTile(clazz);
						}
						else if (shouldBan && HandlerManager.tileEntityBanList.contains(clazz))
						{
							//If original was banned but someone unbanned it in the config
							HandlerManager.tileEntityBanList.remove(clazz);
						}
					}
					catch (Exception e)
					{
						LOGGER.error("ModHandler#loadHandlerData() -> Failed to add entry to config [" + name + " > " + clazz + "]", e);
					}
				}
			}
		}
	}

	private static void loadTileRegistry()
	{
		try
		{
			Field field;
			try
			{
				field = TileEntity.class.getDeclaredField("REGISTRY");
			}
			catch (NoSuchFieldException e)
			{
				field = TileEntity.class.getDeclaredField("field_190562_f");
			}
			field.setAccessible(true);
			TILE_REGISTRY = (RegistryNamespaced) field.get(null);
		}
		catch (NoSuchFieldException e)
		{
			LOGGER.error("ModHandler#loadHandlerData() -> Failed to find the tile registry field. Dumping fields in the clazz, report this error with fields.", e);
			int index = 0;
			for (Field field : TileEntity.class.getDeclaredFields())
			{
				LOGGER.error("\t\tField[" + (index++) + "] -> Name: " + field.getName() + "   Type: " + field.getType());
			}
		}
		catch (IllegalAccessException e)
		{
			LOGGER.error("ModHandler#loadHandlerData() -> Failed to access tile registry\"", e);
		}
		catch (Exception e)
		{
			LOGGER.error("ModHandler#loadHandlerData() -> Unexpected exception while attempting to access tile registry", e);
		}
	}
}
