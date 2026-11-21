package io.github.derexxd.throwing_disc.item;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Supplier;

public enum ThrowingDiscTier {
    COPPER(5.0F, 190, 5, () -> Ingredient.of(Items.COPPER_INGOT)),
    IRON(6.0F, 250, 5, () -> Ingredient.of(Items.IRON_INGOT)),
    GOLDEN(4.5F, 64, 5, () -> Ingredient.of(Items.GOLD_INGOT)),
    DIAMOND(7.0F, 1561, 5, () -> Ingredient.of(Items.DIAMOND)),
    NETHERITE(8.0F, 2031, 5, () -> Ingredient.of(Items.NETHERITE_INGOT)),
    CAPTAIN_AMERICA(10.0F, 3000, 10, () -> Ingredient.of(Items.NETHERITE_INGOT));

    private final float damage;
    private final int durability;
    private final int maxBounces;
    private final Supplier<Ingredient> repairIngredient;

    ThrowingDiscTier(float damage, int durability, int maxBounces, Supplier<Ingredient> repairIngredient) {
        this.damage = damage;
        this.durability = durability;
        this.maxBounces = maxBounces;
        this.repairIngredient = repairIngredient;
    }

    public float getDamage() {
        return this.damage;
    }

    public int getDurability() {
        return this.durability;
    }

    public int getMaxBounces() {
        return this.maxBounces;
    }

    public Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }
}
