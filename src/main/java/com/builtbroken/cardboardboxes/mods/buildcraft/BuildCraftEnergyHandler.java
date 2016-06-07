package com.builtbroken.cardboardboxes.mods.buildcraft;

import com.builtbroken.cardboardboxes.Cardboardboxes;
import com.builtbroken.cardboardboxes.mods.ModSupportHandler;

import java.util.Map;

/**
 * Created by Dark on 7/29/2015.
 */
public class BuildCraftEnergyHandler extends ModSupportHandler
{
    String[] bannedTilesByName = new String[]{"net.minecraft.src.buildcraft.energy.TileEngineWood", "net.minecraft.src.buildcraft.energy.TileEngineCreative"};

    @Override
    public void handleBlackListedContent(Map<String, Class> tileMap)
    {
        Cardboardboxes.LOGGER.info("Loading blacklist support for BuildCraft Energy");
        banTileNames(tileMap, bannedTilesByName);
    }
}
