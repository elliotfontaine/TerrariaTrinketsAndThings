package net.thelunarian.ttandt.items;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;
import net.thelunarian.ttandt.TerrariaTrinketsAndThings;
import net.thelunarian.ttandt.items.custom.MagicMirrorItem;

public class ModItems {
    public static final Item MAGIC_MIRROR = registerItem("magic_mirror",
        new MagicMirrorItem(new FabricItemSettings().group(ModItemGroup.MOD_ITEM_GROUP).maxCount(1).rarity(Rarity.RARE).maxDamage(32)));
    
    private static Item registerItem(String name, Item item){
        return Registry.register(Registry.ITEM, new Identifier(TerrariaTrinketsAndThings.MOD_ID, name), item);
    }

    public static void registerModItems(){
        TerrariaTrinketsAndThings.LOGGER.debug("Registering modded items for " + TerrariaTrinketsAndThings.MOD_ID);
    }
}
