package com.builtbroken.cardboardboxes.handler;

/**
 * Enum of results of attempting to pick up a tile
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/16/2018.
 */
public enum CanPickUpResult
{
    CAN_PICK_UP,
    BANNED_BLOCK,
    BANNED_TILE,
    NO_TILE,
    NO_DATA
}
