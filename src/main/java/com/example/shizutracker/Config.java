package com.example.shizutracker;

import net.neoforged.neoforge.common.ModConfigSpec;
import java.util.List;

public class Config {
    private static final ModConfigSpec.Builder B = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue RADIUS = B
            .comment("Radio de rastreo en bloques (solo ve mobs que el servidor ya te envio)")
            .defineInRange("radius", 64, 8, 512);

    public static final ModConfigSpec.IntValue MAX_LINES = B
            .comment("Maximo de mobs mostrados en el HUD")
            .defineInRange("maxLines", 8, 1, 30);

    public static final ModConfigSpec.BooleanValue START_ENABLED = B
            .comment("Rastreador activado al entrar al mundo")
            .define("startEnabled", false);

    public static final ModConfigSpec.BooleanValue INCLUDE_PLAYERS = B
            .comment("Incluir otros jugadores")
            .define("includePlayers", false);

    public static final ModConfigSpec.BooleanValue GLOW_TARGETS = B
            .comment("Resaltar (brillo visible a traves de paredes) los mobs de la lista 'targets'")
            .define("glowTargets", true);

    public static final ModConfigSpec.BooleanValue GLOW_OTHERS = B
            .comment("Resaltar tambien el resto de mobs rastreados")
            .define("glowOthers", false);

    public static final ModConfigSpec.ConfigValue<List<? extends String>> TARGETS = B
            .comment("Mobs especiales (jefes/raros) en dorado. Formato modid:nombre. Verifica IDs con /summon + TAB")
            .defineListAllowEmpty("targets",
                    List.of("tensura:shizu", "tensura:ifrit", "tensura:hinata_sakaguchi",
                            "tensura:gazel_dwargo", "tensura:orc_lord", "tensura:orc_disaster",
                            "minecraft:wither", "minecraft:ender_dragon", "minecraft:warden"),
                    () -> "modid:mob",
                    o -> o instanceof String);

    public static final ModConfigSpec SPEC = B.build();
}
