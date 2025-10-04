package com.spydnel.backpacks.events;

import com.spydnel.backpacks.Backpacks;
import com.spydnel.backpacks.items.BackpackContainerManager;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@EventBusSubscriber(modid = Backpacks.MODID)
public class ServerTickEvents {

    private static int tickCounter = 0;
    private static final int CLEANUP_INTERVAL = 100; // Run cleanup every 100 ticks (5 seconds)

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        tickCounter++;

        if (tickCounter >= CLEANUP_INTERVAL) {
            // Clean up stale backpack containers
            BackpackContainerManager.cleanup();
            tickCounter = 0;
        }
    }
}
