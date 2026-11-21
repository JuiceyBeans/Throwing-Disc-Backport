package io.github.derexxd.throwing_disc.entity;

import io.github.derexxd.throwing_disc.ThrowingDiscBackport;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class ModEntities {
    public static final ResourceKey<EntityType<?>> THROWING_DISC_KEY = ResourceKey.create(
            Registries.ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(ThrowingDiscBackport.MODID, "throwing_disc")
    );

    public static final EntityType<ThrowingDiscEntity> THROWING_DISC = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            THROWING_DISC_KEY,
            EntityType.Builder.<ThrowingDiscEntity>of(ThrowingDiscEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.15F)
                    .clientTrackingRange(64)
                    .updateInterval(10)
                    .build(THROWING_DISC_KEY)
    );

    public static void register() {
    }
}
