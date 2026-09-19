package com.builtbroken.cardboardboxes.datagen;

import java.util.Set;

import com.builtbroken.cardboardboxes.Cardboardboxes;

import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.recipes.RecipeProvider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = Cardboardboxes.DOMAIN)
public class DataGenRegistrar {
    private DataGenRegistrar() {}

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent.Client event) {
        event.createProvider(BlockTagGenerator::new);
        event.createReloadableRegistryObjects(
            new RegistrySetBuilder().add(RecipeProvider.asBootstrap(RecipeGenerator::new)),
            Set.of(Cardboardboxes.DOMAIN)
        );
    }
}
