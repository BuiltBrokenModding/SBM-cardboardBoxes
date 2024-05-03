package com.builtbroken.cardboardboxes.datagen;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import com.builtbroken.cardboardboxes.Cardboardboxes;
import com.builtbroken.cardboardboxes.Cardboardboxes.TabSortedColors;
import com.builtbroken.cardboardboxes.box.BoxBlockItem;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.RegistryObject;

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

        List<DyeItem> dyes = Arrays.stream(TabSortedColors.values()).map(TabSortedColors::toDyeColor).map(DyeItem::byColor).toList();
        List<BoxBlockItem> colorableBoxes = Cardboardboxes.BOX_ITEM_COLORS.stream().map(RegistryObject::get).toList();

        for (int i = 0; i < dyes.size(); i++) {
            Item dye = dyes.get(i);
            Item box = colorableBoxes.get(i);

            ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, box)
            .requires(dye)
            .requires(Ingredient.of(Stream.concat(Stream.of(new ItemStack(Cardboardboxes.BOX_ITEM.get())), colorableBoxes.stream().filter(item -> !item.equals(box)).map(ItemStack::new))))
            .group("cardboardboxes:colored_boxes")
            .unlockedBy("has_needed_dye", has(dye))
            .save(writer, "dye_" + getItemName(box));
        }
    }
}
