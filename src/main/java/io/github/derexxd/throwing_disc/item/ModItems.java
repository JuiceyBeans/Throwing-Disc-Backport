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

import java.util.function.Function;

public class ModItems {
    public static final Item COPPER_THROWING_DISC = register("copper_throwing_disc",
            properties -> new ThrowingDiscItem(ThrowingDiscTier.COPPER, properties.durability(ThrowingDiscTier.COPPER.getDurability())));

    public static final Item IRON_THROWING_DISC = register("iron_throwing_disc",
            properties -> new ThrowingDiscItem(ThrowingDiscTier.IRON, properties.durability(ThrowingDiscTier.IRON.getDurability())));

    public static final Item GOLDEN_THROWING_DISC = register("golden_throwing_disc",
            properties -> new ThrowingDiscItem(ThrowingDiscTier.GOLDEN, properties.durability(ThrowingDiscTier.GOLDEN.getDurability())));

    public static final Item DIAMOND_THROWING_DISC = register("diamond_throwing_disc",
            properties -> new ThrowingDiscItem(ThrowingDiscTier.DIAMOND, properties.durability(ThrowingDiscTier.DIAMOND.getDurability())));

    public static final Item NETHERITE_THROWING_DISC = register("netherite_throwing_disc",
            properties -> new ThrowingDiscItem(ThrowingDiscTier.NETHERITE, properties.durability(ThrowingDiscTier.NETHERITE.getDurability()).fireResistant()));

    public static final Item CAPTAIN_AMERICA_SHIELD = register("captain_america_shield",
            properties -> new ThrowingDiscItem(ThrowingDiscTier.CAPTAIN_AMERICA, properties.durability(ThrowingDiscTier.CAPTAIN_AMERICA.getDurability()).fireResistant().rarity(Rarity.EPIC)));

    private static Item register(String name, Function<Item.Properties, Item> factory) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(ThrowingDiscBackport.MODID, name));
        Item.Properties properties = new Item.Properties().setId(key);
        Item item = factory.apply(properties);
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
