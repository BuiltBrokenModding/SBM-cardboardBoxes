package com.builtbroken.cardboardboxes.datagen;

import java.util.concurrent.CompletableFuture;

import com.builtbroken.cardboardboxes.Cardboardboxes;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.VanillaBlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.registries.DeferredBlock;

public class BlockTagGenerator extends VanillaBlockTagsProvider {
	public BlockTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(output, lookupProvider);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		tag(BlockTags.BLOCKS_MOTION_NO_LEAVES).add(Cardboardboxes.BOX_BLOCK.getKey()).addAll(Cardboardboxes.BOX_COLORS.map(DeferredBlock::getKey));
	}
}
