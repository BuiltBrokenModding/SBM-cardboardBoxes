package com.builtbroken.cardboardboxes.datagen;

import java.util.concurrent.CompletableFuture;

import com.builtbroken.cardboardboxes.Cardboardboxes;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;

public class RecipeGenerator extends RecipeProvider {
    public RecipeGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    protected final void buildRecipes(RecipeOutput recipeOutput) {
        //@formatter:off
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Cardboardboxes.BOX_ITEM)
        .pattern("PPP")
        .pattern("SWS")
        .pattern("PPP")
        .define('P', Items.PAPER)
        .define('S', Tags.Items.SLIMEBALLS)
        .define('W', ItemTags.LOGS)
        .unlockedBy("has_slimeball", has(Tags.Items.SLIMEBALLS))
        .save(recipeOutput);
        //@formatter:on
    }
}
