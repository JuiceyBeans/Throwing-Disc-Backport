package io.github.derexxd.throwing_disc.client.renderer;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import org.joml.Quaternionf;

public class ThrowingDiscRenderState extends EntityRenderState {
    public final ItemStackRenderState item = new ItemStackRenderState();
    public final Quaternionf orientation = new Quaternionf();
    public float ageInTicks;
}
