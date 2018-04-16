package com.builtbroken.cardboardboxes.mods.buildcraft;

import com.builtbroken.cardboardboxes.Cardboardboxes;
import com.builtbroken.cardboardboxes.mods.ModHandler;
import net.minecraftforge.common.config.Configuration;

/**
 * Created by Dark on 7/29/2015.
 */
public class BuildCraftTransportHandler extends ModHandler
{
    String[] bannedTilesByName = new String[]{"TileGenericPipe"};

    @Override
    public void handleBlackListedContent(Configuration configuration)
    {
        Cardboardboxes.LOGGER.info("Loading blacklist support for BuildCraft Transport");
        //banTileNames(tileMap, bannedTilesByName);
    }
}
