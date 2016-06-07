package com.builtbroken.cardboardboxes.mods.buildcraft;

import com.builtbroken.cardboardboxes.Cardboardboxes;
import com.builtbroken.cardboardboxes.mods.ModSupportHandler;

import java.util.Map;

/**
 * Created by Dark on 7/29/2015.
 */
public class BuildCraftFactoryHandler extends ModSupportHandler
{
    String[] bannedTilesByName = new String[]{"Machine"};

    @Override
    public void handleBlackListedContent(Map<String, Class> tileMap)
    {
        Cardboardboxes.LOGGER.info("Loading blacklist support for BuildCraft Factory");
        banTileNames(tileMap, bannedTilesByName);
    }
}
