package io.github.derexxd.throwing_disc;

import io.github.derexxd.throwing_disc.entity.ModEntities;
import io.github.derexxd.throwing_disc.entity.ThrowingDiscEntity;
import io.github.derexxd.throwing_disc.item.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThrowingDiscBackport implements ModInitializer {
    public static final String MODID = "throwing_disc_backport";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Throwing Disc Backport");
        ModItems.register();
        ModItems.registerDispenserBehaviors();
        ModEntities.register();

        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> ThrowingDiscEntity.recoverForPlayer(newPlayer));
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> ThrowingDiscEntity.recoverForPlayer(handler.player));
    }
}
