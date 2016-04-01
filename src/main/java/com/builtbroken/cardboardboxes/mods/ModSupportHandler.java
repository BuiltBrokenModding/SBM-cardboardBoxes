package com.builtbroken.cardboardboxes.mods;

import com.builtbroken.cardboardboxes.Cardboardboxes;

import java.util.Map;

/**
 * Created by Dark on 7/28/2015.
 */
public class ModSupportHandler
{
    /**
     * Called in the post init phase to handle any blocks that need to be blacklisted
     */
    public void handleBlackListedContent(Map<String, Class> tileMap)
    {

    }

    protected void banTileNames(Map<String, Class> tileMap, String...names)
    {
        for (String name : names)
        {
            if (tileMap.containsKey(name))
            {
                Cardboardboxes.boxHandler.banTile(tileMap.get(name));
            }
            else
            {
                Cardboardboxes.LOGGER.error("\tFailed to locate tile by name " + name + ". This is most likely a mod version issue report this error to mod author so it can be updated");
            }
        }
    }
}
