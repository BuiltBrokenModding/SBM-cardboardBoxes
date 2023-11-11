package com.builtbroken.cardboardboxes.client;

import javax.annotation.Nullable;

import com.builtbroken.cardboardboxes.Cardboardboxes;
import com.builtbroken.cardboardboxes.box.BoxBlock;
import com.builtbroken.cardboardboxes.box.BoxBlockItem;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.registries.RegistryObject;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT, modid = Cardboardboxes.DOMAIN)
public class ClientReg {
    @SubscribeEvent
    public static void registerItemColor(RegisterColorHandlersEvent.Item event) {
        event.register(ClientReg::itemColor, Cardboardboxes.BOX_ITEM_COLORS.stream().map(RegistryObject::get).toArray(Item[]::new));
    }

    @SubscribeEvent
    public static void registerBlockColor(RegisterColorHandlersEvent.Block event) {
        event.register(ClientReg::blockColor, Cardboardboxes.BOX_COLORS.stream().map(RegistryObject::get).toArray(Block[]::new));
    }

    private static int itemColor(ItemStack stack, int tintIndex) {
        if(stack.getItem() instanceof BoxBlockItem box && box.color != null) {
            return box.color.getMapColor().col;
        }
        return -1;
    }

    private static int blockColor(BlockState state, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos, int tintIndex) {
        if(state.getBlock() instanceof BoxBlock box && box.color != null) {
            return box.color.getMapColor().col;
        }
        return -1;
    }
}
