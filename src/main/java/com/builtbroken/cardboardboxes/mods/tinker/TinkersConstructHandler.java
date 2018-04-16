package com.builtbroken.cardboardboxes.mods.tinker;

import com.builtbroken.cardboardboxes.Cardboardboxes;
import com.builtbroken.cardboardboxes.mods.ModHandler;
import net.minecraftforge.common.config.Configuration;

/**
 * Created by Dark on 7/29/2015.
 */
public class TinkersConstructHandler extends ModHandler
{
    String[] bannedTilesByName = new String[]{"TConstruct.Smeltery", "TConstruct.SmelteryDrain", "TConstruct.Servants", "Faucet", "CastingChannel", "Landmine"};

    //TODO add support to pick up entire smeltery, with config to disable feature
    @Override
    public void handleBlackListedContent(Configuration configuration)
    {
        Cardboardboxes.LOGGER.info("Loading blacklist support for TConstruct");
        //banTileNames(tileMap, bannedTilesByName);
    }
}
