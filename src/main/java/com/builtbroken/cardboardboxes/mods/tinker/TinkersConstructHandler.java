package com.builtbroken.cardboardboxes.mods.tinker;

import com.builtbroken.cardboardboxes.Cardboardboxes;
import com.builtbroken.cardboardboxes.mods.ModHandler;

import net.minecraftforge.common.config.Configuration;

/**
 * Handles interaction with Tinkers
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/29/2015.
 */
public class TinkersConstructHandler extends ModHandler
{
    String[] bannedTilesByName = new String[]{"TConstruct.Smeltery", "TConstruct.SmelteryDrain", "TConstruct.Servants", "Faucet", "CastingChannel", "Landmine"};

    //TODO add support to pick up entire smeltery, with config to disable feature
    @Override
    public void load(Configuration configuration)
    {
        Cardboardboxes.LOGGER.info("Loading blacklist support for TConstruct");
        //banTileNames(tileMap, bannedTilesByName);
    }
}
