package com.example.shizutracker;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

/** Mod SOLO CLIENTE: no se carga en servidores dedicados y no hace falta instalarlo en el servidor. */
@Mod(value = ShizuTracker.MODID, dist = Dist.CLIENT)
public class ShizuTracker {
    public static final String MODID = "shizutracker";

    public ShizuTracker(IEventBus modBus, ModContainer container) {
        container.registerConfig(ModConfig.Type.CLIENT, Config.SPEC);
    }
}
