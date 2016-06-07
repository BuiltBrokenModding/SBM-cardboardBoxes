package com.builtbroken.cardboardboxes.mods.buildcraft;

import com.builtbroken.cardboardboxes.Cardboardboxes;
import com.builtbroken.cardboardboxes.mods.ModSupportHandler;

import java.util.Map;

/**
 * Created by Dark on 7/29/2015.
 */
public class BuildCraftBuilderHandler extends ModSupportHandler
{
    String[] bannedTilesByName = new String[]{"Marker", "net.minecraft.src.builders.TileConstructionMarker"};

    @Override
    public void handleBlackListedContent(Map<String, Class> tileMap)
    {
        Cardboardboxes.LOGGER.info("Loading blacklist support for BuildCraft Builders");
        banTileNames(tileMap, bannedTilesByName);
    }
}
