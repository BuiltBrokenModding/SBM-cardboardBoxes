package com.builtbroken.cardboardboxes.mods;

import com.builtbroken.cardboardboxes.Cardboardboxes;

import java.util.Map;

/**
 * Created by Dark on 7/29/2015.
 */
public class IC2Handler extends ModSupportHandler
{
    String[] bannedTilesByName = new String[]{"TECrop", "Nuke"};

    @Override
    public void handleBlackListedContent(Map<String, Class> tileMap)
    {
        Cardboardboxes.LOGGER.info("Loading blacklist support for IC2");
        banTileNames(tileMap, bannedTilesByName);
    }
}
