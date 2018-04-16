package com.builtbroken.cardboardboxes.mods.ic2;

import com.builtbroken.cardboardboxes.Cardboardboxes;
import com.builtbroken.cardboardboxes.mods.ModHandler;
import net.minecraftforge.common.config.Configuration;

/**
 * Created by Dark on 7/29/2015.
 */
public class IC2Handler extends ModHandler
{
    String[] bannedTilesByName = new String[]{"TECrop", "Nuke"};

    @Override
    public void handleBlackListedContent(Configuration configuration)
    {
        Cardboardboxes.LOGGER.info("Loading blacklist support for IC2");
        //banTileNames(bannedTilesByName);
    }
}
