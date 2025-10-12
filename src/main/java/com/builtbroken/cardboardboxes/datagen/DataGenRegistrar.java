package com.builtbroken.cardboardboxes.datagen;

import com.builtbroken.cardboardboxes.Cardboardboxes;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = Cardboardboxes.DOMAIN)
public class DataGenRegistrar {
    private DataGenRegistrar() {}

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent.Client event) {
        event.createProvider(RecipeGenerator.Runner::new);
    }
}
