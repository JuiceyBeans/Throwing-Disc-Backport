package io.github.derexxd.throwing_disc.item;

import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public record ThrowingDiscTier(
        float damage,
        int durability,
        int maxBounces,
        float velocity,
        TagKey<Item> repairMaterials
) {
    public static final ThrowingDiscTier COPPER = new ThrowingDiscTier(3.0F, 190, 5, 1.3F, ItemTags.COPPER_TOOL_MATERIALS);
    public static final ThrowingDiscTier IRON = new ThrowingDiscTier(4.0F, 250, 4, 1.1F, ItemTags.IRON_TOOL_MATERIALS);
    public static final ThrowingDiscTier GOLDEN = new ThrowingDiscTier(2.5F, 64, 5, 2.0F, ItemTags.GOLD_TOOL_MATERIALS);
    public static final ThrowingDiscTier DIAMOND = new ThrowingDiscTier(5.0F, 1561, 3, 0.9F, ItemTags.DIAMOND_TOOL_MATERIALS);
    public static final ThrowingDiscTier NETHERITE = new ThrowingDiscTier(6.0F, 2031, 2, 0.75F, ItemTags.NETHERITE_TOOL_MATERIALS);
    public static final ThrowingDiscTier CAPTAIN_AMERICA = new ThrowingDiscTier(8.0F, 3000, 4, 1.1F, ItemTags.NETHERITE_TOOL_MATERIALS);

    public Item.Properties applyProperties(Item.Properties properties) {
        return properties
                .durability(this.durability)
                .repairable(this.repairMaterials)
                .attributes(ItemAttributeModifiers.builder()
                        .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, this.damage,
                                AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                        .build());
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

    public float getVelocity() {
        return this.velocity;
    }

    public TagKey<Item> getRepairMaterials() {
        return this.repairMaterials;
    }
}
