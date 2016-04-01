package com.builtbroken.cardboardboxes.mods.buildcraft;

import com.builtbroken.cardboardboxes.Cardboardboxes;
import com.builtbroken.cardboardboxes.mods.ModSupportHandler;

import java.util.Map;

/**
 * Created by Dark on 7/29/2015.
 */
public class BuildCraftTransportHandler extends ModSupportHandler
{
    String[] bannedTilesByName = new String[]{"TileGenericPipe"};

    @Override
    public void handleBlackListedContent(Map<String, Class> tileMap)
    {
        Cardboardboxes.LOGGER.info("Loading blacklist support for BuildCraft Transport");
        banTileNames(tileMap, bannedTilesByName);
    }
}
