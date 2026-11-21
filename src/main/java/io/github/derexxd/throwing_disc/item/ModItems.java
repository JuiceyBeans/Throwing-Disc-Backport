package io.github.derexxd.throwing_disc.item;

import io.github.derexxd.throwing_disc.ThrowingDiscBackport;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import java.util.function.UnaryOperator;

public class ModItems {
    public static final Item COPPER_THROWING_DISC = register("copper_throwing_disc", ThrowingDiscTier.COPPER);

    public static final Item IRON_THROWING_DISC = register("iron_throwing_disc", ThrowingDiscTier.IRON);

    public static final Item GOLDEN_THROWING_DISC = register("golden_throwing_disc", ThrowingDiscTier.GOLDEN);

    public static final Item DIAMOND_THROWING_DISC = register("diamond_throwing_disc", ThrowingDiscTier.DIAMOND);

    public static final Item NETHERITE_THROWING_DISC = register("netherite_throwing_disc", ThrowingDiscTier.NETHERITE,
            properties -> properties.fireResistant());

    public static final Item CAPTAIN_AMERICA_SHIELD = register("captain_america_shield", ThrowingDiscTier.CAPTAIN_AMERICA,
            properties -> properties.fireResistant().rarity(Rarity.EPIC));

    private static Item register(String name, ThrowingDiscTier tier) {
        return register(name, tier, properties -> properties);
    }

    private static Item register(String name, ThrowingDiscTier tier, UnaryOperator<Item.Properties> properties) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(ThrowingDiscBackport.MODID, name));
        Item.Properties props = properties.apply(tier.apply(new Item.Properties().setId(key)));
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
}
