package com.builtbroken.cardboardboxes.datagen;

import java.util.function.Consumer;

import com.builtbroken.cardboardboxes.Cardboardboxes;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;

public class RecipeGenerator extends RecipeProvider {
    public RecipeGenerator(PackOutput output) {
        super(output);
    }

    @Override
    protected final void buildRecipes(Consumer<FinishedRecipe> writer) {
        //@formatter:off
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Cardboardboxes.BOX_ITEM.get())
        .pattern("PPP")
        .pattern("SWS")
        .pattern("PPP")
        .define('P', Items.PAPER)
        .define('S', Tags.Items.SLIMEBALLS)
        .define('W', ItemTags.LOGS)
        .unlockedBy("has_slimeball", has(Tags.Items.SLIMEBALLS))
        .save(writer);
        //@formatter:on
    }
}
