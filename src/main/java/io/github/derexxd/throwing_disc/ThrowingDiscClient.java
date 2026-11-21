package io.github.derexxd.throwing_disc;

import io.github.derexxd.throwing_disc.client.renderer.ThrowingDiscRenderer;
import io.github.derexxd.throwing_disc.entity.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class ThrowingDiscClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntities.THROWING_DISC, ThrowingDiscRenderer::new);
    }
}
