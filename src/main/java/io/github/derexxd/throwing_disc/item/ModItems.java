package io.github.derexxd.throwing_disc.item;

import io.github.derexxd.throwing_disc.ThrowingDiscBackport;
import io.github.derexxd.throwing_disc.entity.ThrowingDiscEntity;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.UseCooldown;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.function.UnaryOperator;

public class ModItems {
    public static final Item COPPER_THROWING_DISC = register("copper_throwing_disc", ThrowingDiscTier.COPPER);

    public static final Item IRON_THROWING_DISC = register("iron_throwing_disc", ThrowingDiscTier.IRON);

    public static final Item GOLDEN_THROWING_DISC = register("golden_throwing_disc", ThrowingDiscTier.GOLDEN);

    public static final Item DIAMOND_THROWING_DISC = register("diamond_throwing_disc", ThrowingDiscTier.DIAMOND);

    public static final Item NETHERITE_THROWING_DISC = register("netherite_throwing_disc", ThrowingDiscTier.NETHERITE,
            Item.Properties::fireResistant);

    public static final Item CAPTAIN_AMERICA_SHIELD = register("captain_america_shield", ThrowingDiscTier.CAPTAIN_AMERICA,
            properties -> properties.fireResistant().rarity(Rarity.EPIC));

    private static Item register(String name, ThrowingDiscTier tier) {
        return register(name, tier, properties -> properties);
    }

    private static Item register(String name, ThrowingDiscTier tier, UnaryOperator<Item.Properties> properties) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(ThrowingDiscBackport.MODID, name));
        Item.Properties props = properties.apply(tier.applyProperties(new Item.Properties().setId(key)))
                .component(DataComponents.USE_COOLDOWN, new UseCooldown(1.0F, Optional.of(Identifier.fromNamespaceAndPath(ThrowingDiscBackport.MODID, "throwing_disc"))));
        Item item = new ThrowingDiscItem(tier, props);
        return Registry.register(BuiltInRegistries.ITEM, key, item);
    }

    public static void register() {
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT).register(output -> {
            output.accept(COPPER_THROWING_DISC);
            output.accept(IRON_THROWING_DISC);
            output.accept(GOLDEN_THROWING_DISC);
            output.accept(DIAMOND_THROWING_DISC);
            output.accept(NETHERITE_THROWING_DISC);
        });
    }

    public static void registerDispenserBehaviors() {
        DefaultDispenseItemBehavior behavior = new DefaultDispenseItemBehavior() {
            @Override
            protected ItemStack execute(BlockSource source, ItemStack stack) {
                Direction facing = source.state().getValue(DispenserBlock.FACING);
                ServerLevel level = source.level();
                Vec3 pos = source.center().add(
                        facing.getStepX() * 0.7,
                        facing.getStepY() * 0.7,
                        facing.getStepZ() * 0.7
                );

                ThrowingDiscEntity disc = new ThrowingDiscEntity(level, pos.x, pos.y, pos.z, stack.copyWithCount(1));
                disc.setDeltaMovement(
                        facing.getStepX() * 1.5,
                        facing.getStepY() * 1.5,
                        facing.getStepZ() * 1.5
                );

                level.addFreshEntity(disc);
                stack.shrink(1);
                return stack;
            }

            @Override
            protected void playSound(BlockSource source) {
                source.level().levelEvent(1000, source.pos(), 0);
            }
        };

        DispenserBlock.registerBehavior(COPPER_THROWING_DISC, behavior);
        DispenserBlock.registerBehavior(IRON_THROWING_DISC, behavior);
        DispenserBlock.registerBehavior(GOLDEN_THROWING_DISC, behavior);
        DispenserBlock.registerBehavior(DIAMOND_THROWING_DISC, behavior);
        DispenserBlock.registerBehavior(NETHERITE_THROWING_DISC, behavior);
        DispenserBlock.registerBehavior(CAPTAIN_AMERICA_SHIELD, behavior);
    }
}
