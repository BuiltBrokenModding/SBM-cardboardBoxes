package com.builtbroken.cardboardboxes.mods.ic2;

import com.builtbroken.cardboardboxes.Cardboardboxes;
import com.builtbroken.cardboardboxes.mods.ModHandler;

import net.minecraftforge.common.config.Configuration;

/** Handles interaction with IC2
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/29/2015.
 */
public class IC2Handler extends ModHandler
{
    String[] bannedTilesByName = new String[]{"TECrop", "Nuke"};

    @Override
    public void load(Configuration configuration)
    {
        Cardboardboxes.LOGGER.info("Loading blacklist support for IC2");
        //banTileNames(bannedTilesByName);
    }
}
