package com.example.shizutracker;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import org.lwjgl.glfw.GLFW;

import java.util.*;

public class TrackerClient {
    public enum Mode { ALL, TARGETS }

    public record Entry(Entity entity, boolean target, double dist) {}

    private static final String[] ARROWS = {"⬆", "⬈", "➡", "⬊", "⬇", "⬋", "⬅", "⬉"};

    public static final KeyMapping TOGGLE_KEY = new KeyMapping(
            "key.shizutracker.toggle", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_K, "key.categories.shizutracker");
    public static final KeyMapping MODE_KEY = new KeyMapping(
            "key.shizutracker.mode", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_J, "key.categories.shizutracker");

    static boolean enabled = false;
    static boolean initialized = false;
    static Mode mode = Mode.ALL;
    static List<Entry> entries = new ArrayList<>();
    static Set<Entity> glowing = Collections.newSetFromMap(new IdentityHashMap<>());
    static int tickCounter = 0;

    /** Se llama cada tick del cliente. */
    public static void tick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        if (!initialized) {
            initialized = true;
            enabled = Config.START_ENABLED.get();
        }

        while (TOGGLE_KEY.consumeClick()) {
            enabled = !enabled;
            if (!enabled) clear();
            mc.player.displayClientMessage(Component.translatable(
                    enabled ? "shizutracker.on" : "shizutracker.off"), true);
        }
        while (MODE_KEY.consumeClick()) {
            mode = (mode == Mode.ALL) ? Mode.TARGETS : Mode.ALL;
            mc.player.displayClientMessage(Component.translatable(
                    "shizutracker.mode." + mode.name().toLowerCase()), true);
            tickCounter = 99; // reescanear ya
        }

        if (!enabled) return;
        if (++tickCounter >= 10) { // escanea 2 veces por segundo
            tickCounter = 0;
            scan(mc.player, mc.level);
        }
    }

    private static void scan(LocalPlayer p, ClientLevel level) {
        double r = Config.RADIUS.get();
        double r2 = r * r;

        Set<ResourceLocation> targets = new HashSet<>();
        for (String s : Config.TARGETS.get()) {
            ResourceLocation id = ResourceLocation.tryParse(s);
            if (id != null) targets.add(id);
        }

        List<Entry> list = new ArrayList<>();
        Set<Entity> newGlow = Collections.newSetFromMap(new IdentityHashMap<>());

        for (Entity e : level.entitiesForRendering()) {
            if (!(e instanceof LivingEntity le) || e == p || e instanceof ArmorStand || !le.isAlive()) continue;
            if (e instanceof Player && !Config.INCLUDE_PLAYERS.get()) continue;
            double d2 = e.distanceToSqr(p);
            if (d2 > r2) continue;

            boolean isTarget = targets.contains(BuiltInRegistries.ENTITY_TYPE.getKey(e.getType()));
            if (mode == Mode.TARGETS && !isTarget) continue;

            list.add(new Entry(e, isTarget, Math.sqrt(d2)));
            if (isTarget ? Config.GLOW_TARGETS.get() : Config.GLOW_OTHERS.get()) newGlow.add(e);
        }

        // objetivos especiales primero, luego por distancia
        list.sort(Comparator.comparing((Entry en) -> !en.target()).thenComparingDouble(Entry::dist));
        entries = list;

        for (Entity e : glowing) if (!newGlow.contains(e)) e.setGlowingTag(false);
        for (Entity e : newGlow) e.setGlowingTag(true);
        glowing = newGlow;
    }

    public static void clear() {
        for (Entity e : glowing) e.setGlowingTag(false);
        glowing = Collections.newSetFromMap(new IdentityHashMap<>());
        entries = new ArrayList<>();
        tickCounter = 0;
    }

    public static void reset() {
        clear();
        initialized = false;
    }

    /** Capa del HUD. */
    public static void renderHud(GuiGraphics g, DeltaTracker dt) {
        Minecraft mc = Minecraft.getInstance();
        if (!enabled || mc.player == null || mc.options.hideGui) return;

        int x = 6, y = 6;
        g.drawString(mc.font, Component.translatable("shizutracker.hud.title", mode.name(), entries.size()),
                x, y, 0x55FFFF, true);
        y += 12;

        int shown = 0;
        for (Entry en : entries) {
            if (shown++ >= Config.MAX_LINES.get()) break;
            Entity e = en.entity();
            double dx = e.getX() - mc.player.getX();
            double dz = e.getZ() - mc.player.getZ();
            float targetYaw = (float) Math.toDegrees(Math.atan2(-dx, dz));
            float rel = Mth.wrapDegrees(targetYaw - mc.player.getYRot());
            String arrow = ARROWS[Math.floorMod(Math.round(rel / 45f), 8)];

            String line = arrow + " " + e.getDisplayName().getString() + " " + (int) en.dist() + "m";
            g.drawString(mc.font, line, x, y, en.target() ? 0xFFAA00 : 0xFFFFFF, true);
            y += 10;
        }
    }
}
