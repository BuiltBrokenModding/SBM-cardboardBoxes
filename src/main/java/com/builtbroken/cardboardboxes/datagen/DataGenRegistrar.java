package com.builtbroken.cardboardboxes.datagen;

import com.builtbroken.cardboardboxes.Cardboardboxes;

import net.minecraft.data.recipes.RecipeProvider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.data.event.GatherDataRegistryEntriesEvent;

@EventBusSubscriber(modid = Cardboardboxes.DOMAIN)
public class DataGenRegistrar {
    private DataGenRegistrar() {}

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent.Client event) {
        event.createProvider(BlockTagGenerator::new);
    }

    @SubscribeEvent
    public static void onGatherDataRegistryEntries(GatherDataRegistryEntriesEvent event) {
            event.add(RecipeProvider.asBootstrap(RecipeGenerator::new));
    }
}
