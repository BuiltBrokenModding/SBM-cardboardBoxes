package com.builtbroken.cardboardboxes.mods;

import com.builtbroken.cardboardboxes.Cardboardboxes;

import java.util.Map;

/**
 * Created by Dark on 7/29/2015.
 */
public class TinkersConstructHandler extends ModSupportHandler
{
    String[] bannedTilesByName = new String[]{"TConstruct.Smeltery", "TConstruct.SmelteryDrain", "TConstruct.Servants", "Faucet", "CastingChannel", "Landmine"};

    //TODO add support to pick up entire smeltery, with config to disable feature
    @Override
    public void handleBlackListedContent(Map<String, Class> tileMap)
    {
        Cardboardboxes.LOGGER.info("Loading blacklist support for TConstruct");
        banTileNames(tileMap, bannedTilesByName);
    }
}
