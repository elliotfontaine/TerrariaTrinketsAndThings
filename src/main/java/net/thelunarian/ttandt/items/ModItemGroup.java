package net.thelunarian.ttandt.items;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.thelunarian.ttandt.TerrariaTrinketsAndThings;

public class ModItemGroup {
    public static final ItemGroup MOD_ITEM_GROUP = FabricItemGroupBuilder.build(
            new Identifier(TerrariaTrinketsAndThings.MOD_ID, "ttandt"),
            () -> new ItemStack(ModItems.MAGIC_MIRROR));
}
