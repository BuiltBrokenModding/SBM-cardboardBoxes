package com.builtbroken.cardboardboxes.mods.buildcraft;

import com.builtbroken.cardboardboxes.Cardboardboxes;
import com.builtbroken.cardboardboxes.mods.ModHandler;
import net.minecraftforge.common.config.Configuration;

/**
 * Handles interaction for Buildcraft energy module
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/29/2015.
 */
public class BuildCraftEnergyHandler extends ModHandler
{
    String[] bannedTilesByName = new String[]{"net.minecraft.src.buildcraft.energy.TileEngineWood", "net.minecraft.src.buildcraft.energy.TileEngineCreative"};

    @Override
    public void load(Configuration configuration)
    {
        Cardboardboxes.LOGGER.info("Loading blacklist support for BuildCraft Energy");
        //banTileNames(tileMap, bannedTilesByName);
    }
}
