package com.builtbroken.cardboardboxes.datagen;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.builtbroken.cardboardboxes.Cardboardboxes;

import net.minecraft.DetectedVersion;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = Cardboardboxes.DOMAIN, bus = Bus.MOD)
public class DataGenRegistrar {
    private DataGenRegistrar() {}

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();

        generator.addProvider(event.includeServer(), new RecipeGenerator(output));
        generator.addProvider(true, new PackMetadataGenerator(output)
                .add(PackMetadataSection.TYPE, new PackMetadataSection(Component.literal("cardboard boxes resources & data"),
                        DetectedVersion.BUILT_IN.getPackVersion(PackType.CLIENT_RESOURCES),
                        Arrays.stream(PackType.values()).collect(Collectors.toMap(Function.identity(), DetectedVersion.BUILT_IN::getPackVersion)))));
    }
}
