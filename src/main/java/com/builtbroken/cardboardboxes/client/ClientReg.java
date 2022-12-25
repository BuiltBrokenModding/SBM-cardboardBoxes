package com.builtbroken.cardboardboxes.client;

import com.builtbroken.cardboardboxes.Cardboardboxes;
import com.builtbroken.cardboardboxes.box.BoxBlock;
import com.builtbroken.cardboardboxes.box.BoxBlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT, modid = Cardboardboxes.DOMAIN)
public class ClientReg {

    @SubscribeEvent
    public static void registerItemColor(RegisterColorHandlersEvent.Item event)
    {
        event.register(ClientReg::itemColor, Cardboardboxes.BOX_ITEM_COLORS.stream().map(RegistryObject::get).toArray(Item[]::new));
    }

    @SubscribeEvent
    public static void registerBlockColor(RegisterColorHandlersEvent.Block event)
    {
        event.register(ClientReg::blockColor, Cardboardboxes.BOX_COLORS.stream().map(RegistryObject::get).toArray(Block[]::new));
    }

    private static int itemColor(ItemStack pStack, int pTintIndex) {
        if(pStack.getItem() instanceof BoxBlockItem box && box.color != null) {
            return box.color.getMaterialColor().col;
        }
        return -1;
    }

    private static int blockColor(BlockState pState, @Nullable BlockAndTintGetter pLevel, @Nullable BlockPos pPos, int pTintIndex) {
        if(pState.getBlock() instanceof BoxBlock box && box.color != null) {
            return box.color.getMaterialColor().col;
        }
        return -1;
    }
}
