package com.builtbroken.cardboardboxes.client;

import java.util.List;

import com.builtbroken.cardboardboxes.Cardboardboxes;
import com.builtbroken.cardboardboxes.box.BoxBlock;

import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.registries.DeferredBlock;

@EventBusSubscriber(value = Dist.CLIENT, modid = Cardboardboxes.DOMAIN)
public class ClientReg {
    @SubscribeEvent
    public static void registerItemTintSource(RegisterColorHandlersEvent.ItemTintSources event) {
        event.register(Identifier.fromNamespaceAndPath(Cardboardboxes.DOMAIN, "box_color"), BoxColor.MAP_CODEC);
    }

    @SubscribeEvent
    public static void registerBlockColor(RegisterColorHandlersEvent.BlockTintSources event) {
        event.register(List.of(ClientReg::blockColor), Cardboardboxes.BOX_COLORS.asList().stream().map(DeferredBlock::get).toArray(Block[]::new));
    }

    private static int blockColor(BlockState state) {
        if (state.getBlock() instanceof BoxBlock box && box.color != null) {
            return box.color.getMapColor().col;
        }
        return -1;
    }
}
