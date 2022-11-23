package net.thelunarian.ttandt;

import net.fabricmc.api.ModInitializer;
import net.thelunarian.ttandt.config.ModConfig;
import net.thelunarian.ttandt.items.ModItems;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.shedaniel.autoconfig.ConfigHolder;

public class TerrariaTrinketsAndThings implements ModInitializer {
	public static final String MOD_ID = "ttandt";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final ConfigHolder<ModConfig> CONFIG = ModConfig.init();
    public static ModConfig getConfig() {return CONFIG.getConfig();}

	@Override
	public void onInitialize() {
		ModItems.registerModItems();
	}
}
