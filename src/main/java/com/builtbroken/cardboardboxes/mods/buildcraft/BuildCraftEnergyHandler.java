package com.builtbroken.cardboardboxes.mods.buildcraft;

import com.builtbroken.cardboardboxes.Cardboardboxes;
import com.builtbroken.cardboardboxes.mods.ModHandler;
import net.minecraftforge.common.config.Configuration;

/**
 * Created by Dark on 7/29/2015.
 */
public class BuildCraftEnergyHandler extends ModHandler
{
    String[] bannedTilesByName = new String[]{"net.minecraft.src.buildcraft.energy.TileEngineWood", "net.minecraft.src.buildcraft.energy.TileEngineCreative"};

    @Override
    public void handleBlackListedContent(Configuration configuration)
    {
        Cardboardboxes.LOGGER.info("Loading blacklist support for BuildCraft Energy");
        //banTileNames(tileMap, bannedTilesByName);
    }
}
