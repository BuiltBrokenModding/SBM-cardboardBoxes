package com.builtbroken.cardboardboxes.datagen;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import com.builtbroken.cardboardboxes.Cardboardboxes;
import com.builtbroken.cardboardboxes.Cardboardboxes.TabSortedColors;
import com.builtbroken.cardboardboxes.box.BoxBlockItem;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredItem;

public class RecipeGenerator extends RecipeProvider {
    private final HolderGetter<Item> items;

    public RecipeGenerator(HolderLookup.Provider lookupProvider, RecipeOutput output) {
        super(lookupProvider, output);
        items = lookupProvider.lookupOrThrow(Registries.ITEM);
    }

    @Override
    protected final void buildRecipes() {
        //@formatter:off
        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, Cardboardboxes.BOX_ITEM)
        .pattern("PPP")
        .pattern("SWS")
        .pattern("PPP")
        .define('P', Items.PAPER)
        .define('S', Tags.Items.SLIME_BALLS)
        .define('W', ItemTags.LOGS)
        .unlockedBy("has_slimeball", has(Tags.Items.SLIME_BALLS))
        .save(output);
        //@formatter:on

        List<DyeItem> dyes = Arrays.stream(TabSortedColors.values()).map(TabSortedColors::toDyeColor).map(DyeItem::byColor).toList();
        List<BoxBlockItem> colorableBoxes = Cardboardboxes.BOX_ITEM_COLORS.stream().map(DeferredItem::get).toList();

        for (int i = 0; i < dyes.size(); i++) {
            Item dye = dyes.get(i);
            Item box = colorableBoxes.get(i);

            ShapelessRecipeBuilder.shapeless(items, RecipeCategory.BUILDING_BLOCKS, box)
            .requires(dye)
            .requires(Ingredient.of(Stream.concat(Stream.of(Cardboardboxes.BOX_ITEM.get()), colorableBoxes.stream().filter(item -> !item.equals(box)))))
            .group("cardboardboxes:colored_boxes")
            .unlockedBy("has_needed_dye", has(dye))
            .save(output, "dye_" + getItemName(box));
        }
    }
    public static final class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(output, lookupProvider);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider lookupProvider, RecipeOutput output) {
            return new RecipeGenerator(lookupProvider, output);
        }

        @Override
        public String getName() {
            return "SecurityCraft recipes";
        }
    }
}
