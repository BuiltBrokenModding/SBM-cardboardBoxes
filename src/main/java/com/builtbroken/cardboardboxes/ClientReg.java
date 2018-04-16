package com.builtbroken.cardboardboxes;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Handles registering client side content
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/16/2018.
 */
@Mod.EventBusSubscriber(value = Side.CLIENT, modid = Cardboardboxes.DOMAIN)
public class ClientReg
{
    @SubscribeEvent
    public static void registerModel(ModelRegistryEvent event)
    {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(Cardboardboxes.blockBox), 0, new ModelResourceLocation(Cardboardboxes.blockBox.getRegistryName(), "inventory"));
    }
}
