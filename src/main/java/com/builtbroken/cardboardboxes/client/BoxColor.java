package com.builtbroken.cardboardboxes.client;

import com.builtbroken.cardboardboxes.box.BoxBlockItem;
import com.mojang.serialization.MapCodec;

import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public record BoxColor() implements ItemTintSource {
    public static final MapCodec<BoxColor> MAP_CODEC = MapCodec.unit(new BoxColor());

    @Override
    public int calculate(ItemStack stack, ClientLevel level, LivingEntity entity) {
        if (stack.getItem() instanceof BoxBlockItem box && box.color != null) {
            return ARGB.color(255, box.color.getMapColor().col);
        }
        return -1;
    }

    @Override
    public MapCodec<? extends ItemTintSource> type() {
        return MAP_CODEC;
    }
}
