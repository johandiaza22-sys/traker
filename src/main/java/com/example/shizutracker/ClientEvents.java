package com.example.shizutracker;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

public class ClientEvents {

    @EventBusSubscriber(modid = ShizuTracker.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModBus {
        @SubscribeEvent
        public static void keys(RegisterKeyMappingsEvent e) {
            e.register(TrackerClient.TOGGLE_KEY);
            e.register(TrackerClient.MODE_KEY);
        }

        @SubscribeEvent
        public static void gui(RegisterGuiLayersEvent e) {
            e.registerAboveAll(ResourceLocation.fromNamespaceAndPath(ShizuTracker.MODID, "hud"),
                    TrackerClient::renderHud);
        }
    }

    @EventBusSubscriber(modid = ShizuTracker.MODID, value = Dist.CLIENT)
    public static class GameBus {
        @SubscribeEvent
        public static void tick(ClientTickEvent.Post e) {
            TrackerClient.tick();
        }

        @SubscribeEvent
        public static void logout(ClientPlayerNetworkEvent.LoggingOut e) {
            TrackerClient.reset();
        }
    }
}
