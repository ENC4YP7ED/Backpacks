package com.spydnel.backpacks.events;

import com.spydnel.backpacks.Backpacks;
import com.spydnel.backpacks.items.BackpackContainerManager;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@EventBusSubscriber(modid = Backpacks.MODID)
public class ServerTickEvents {

    private static int tickCounter = 0;
    private static int validationTickCounter = 0;

    private static final int CLEANUP_INTERVAL = 100; // Run cleanup every 100 ticks (5 seconds)
    private static final int VALIDATION_INTERVAL = 20; // Validate containers every 20 ticks (1 second)

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        tickCounter++;
        validationTickCounter++;

        // Validate containers every second to kick viewers if backpack is removed
        if (validationTickCounter >= VALIDATION_INTERVAL) {
            BackpackContainerManager.validateAllContainers();
            validationTickCounter = 0;
        }

        // Clean up stale backpack containers every 5 seconds
        if (tickCounter >= CLEANUP_INTERVAL) {
            BackpackContainerManager.cleanup();
            tickCounter = 0;
        }
    }
}
