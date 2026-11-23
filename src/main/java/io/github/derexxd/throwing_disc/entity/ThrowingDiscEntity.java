package io.github.derexxd.throwing_disc.entity;

import io.github.derexxd.throwing_disc.item.ModItems;
import io.github.derexxd.throwing_disc.item.ThrowingDiscItem;
import io.github.derexxd.throwing_disc.item.ThrowingDiscTier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@SuppressWarnings("NullableProblems")
public class ThrowingDiscEntity extends ThrowableItemProjectile {
    private static final EntityDataAccessor<Boolean> ID_RETURNING = SynchedEntityData.defineId(ThrowingDiscEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> ID_DISPENSED = SynchedEntityData.defineId(ThrowingDiscEntity.class, EntityDataSerializers.BOOLEAN);
    private static final Set<ThrowingDiscEntity> ACTIVE_DISCS = new HashSet<>();
    private InteractionHand thrownHand = InteractionHand.MAIN_HAND;
    private UUID ownerUUID;
    private int flightTicks = 0;
    private int bounces = 0;
    private double distanceTraveled = 0.0; // per bounce
    private final Set<UUID> hitEntities = new HashSet<>();

    public ThrowingDiscEntity(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
        this.setNoGravity(true);
    }

    public ThrowingDiscEntity(Level level, double x, double y, double z, ItemStack stack) {
        super(ModEntities.THROWING_DISC, level);
        this.setPos(x, y, z);
        this.setItem(stack.copy());
        this.setNoGravity(true);
        this.entityData.set(ID_DISPENSED, true);
    }

    public ThrowingDiscEntity(Level level, LivingEntity shooter, ItemStack stack, InteractionHand hand) {
        super(ModEntities.THROWING_DISC, shooter, level, stack.copy());
        this.thrownHand = hand;
        this.ownerUUID = shooter.getUUID();
        this.setNoGravity(true);
        if (!level.isClientSide()) {
            ACTIVE_DISCS.add(this);
        }
    }

    public ThrowingDiscTier getTier() {
        return this.getItem().getItem() instanceof ThrowingDiscItem disc ? disc.getTier() : ThrowingDiscTier.IRON;
    }

    public InteractionHand getThrownHand() {
        return this.thrownHand;
    }

    public boolean isReturning() {
        return this.entityData.get(ID_RETURNING);
    }

    public void setReturning(boolean returning) {
        this.entityData.set(ID_RETURNING, returning);
        if (returning) {
            this.hitEntities.clear();
            this.noPhysics = true;
        }
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.IRON_THROWING_DISC;
    }

    @Override
    protected double getDefaultGravity() {
        return 0.0;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ID_RETURNING, false);
        builder.define(ID_DISPENSED, false);
    }

    @Override
    public void tick() {
        this.baseTick();
        this.flightTicks++;

        if (isReturning()) {
            this.noPhysics = true;

            if (!level().isClientSide() && this.ownerUUID != null) ACTIVE_DISCS.add(this);

            Entity owner = getOwner();

            if (owner instanceof Player player && owner.isAlive()) {
                Vec3 target = owner.getEyePosition().subtract(0.0, 0.2, 0.0);
                Vec3 dir = target.subtract(position());

                if (!level().isClientSide()) {
                    if (this.flightTicks > 240) {
                        recoverOrDrop(owner);
                        discard();
                        return;
                    }

                    if (dir.length() < 1.3) {
                        if (!player.getAbilities().instabuild) {
                            ItemStack returnStack = getItem();
                            if (player.getItemInHand(this.thrownHand).isEmpty()) {
                                player.setItemInHand(this.thrownHand, returnStack);
                            } else if (!player.getInventory().add(returnStack)) {
                                player.drop(returnStack, false);
                            }
                        }
                        level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.TRIDENT_RETURN, SoundSource.PLAYERS, 0.6F, 1.0F);
                        discard();
                        return;
                    }
                }

                Vec3 desiredVelocity = dir.normalize().scale(Math.min(1.2, 0.5 + (this.flightTicks * 0.02)));
                setDeltaMovement(getDeltaMovement().lerp(desiredVelocity, 0.28));

            } else if (!level().isClientSide()){
                recoverOrDrop(owner);
                discard();
                return;
            }

            Vec3 start = position();
            Vec3 motion = getDeltaMovement();
            Vec3 end = start.add(motion);

            EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(
                    level(),
                    this,
                    start,
                    end,
                    getBoundingBox().expandTowards(motion).inflate(1.0),
                    this::canHitEntity
            );

            if (entityHit != null) {
                onHitEntity(entityHit);
            }

            setPos(end.x, end.y, end.z);
            return;
        }

        if (!level().isClientSide()) {
            if (this.entityData.get(ID_DISPENSED)) {
                if (this.flightTicks >= 25) {
                    dropAsItem();
                    return;
                }
            } else {
                if (this.ownerUUID != null) {
                    ACTIVE_DISCS.add(this);
                }
                if (this.flightTicks > 240) {
                    recoverOrDrop(getOwner());
                    discard();
                    return;
                }
            }
        }

        Vec3 start = position();
        Vec3 motion = getDeltaMovement();

        if (!this.entityData.get(ID_DISPENSED)) {
            double remaining = 10 - this.distanceTraveled;

            if (remaining <= 1.0E-4) {
                if (!level().isClientSide()) setReturning(true);
                return;
            }

            if (motion.length() > remaining) {
                motion = motion.normalize().scale(Math.max(remaining, 0.0));
            }
        }

        Vec3 end = start.add(motion);

        if (!level().isClientSide()) harvestCrops(start, motion);

        BlockHitResult blockHit = level().clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        Vec3 rayLimit = blockHit.getType() != HitResult.Type.MISS ? blockHit.getLocation() : end;

        EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(
                level(),
                this,
                start,
                rayLimit,
                getBoundingBox().expandTowards(motion).inflate(1.0),
                this::canHitEntity
        );

        if (entityHit != null) {
            onHitEntity(entityHit);
        }

        if (blockHit.getType() != HitResult.Type.MISS) {
            onHitBlock(blockHit);
        } else {
            setPos(end.x, end.y, end.z);
        }

        this.distanceTraveled += position().distanceTo(start);

        if (!level().isClientSide() && this.distanceTraveled >= 10.0 - 1.0E-4) setReturning(true);
    }

    private void recoverOrDrop(Entity owner) {
        if (owner instanceof Player player && owner.isAlive()) {
            if (!player.getAbilities().instabuild) {
                ItemStack returnStack = getItem();
                if (player.getItemInHand(this.thrownHand).isEmpty()) {
                    player.setItemInHand(this.thrownHand, returnStack);
                } else if (!player.getInventory().add(returnStack)) {
                    player.drop(returnStack, false);
                }
            }
        } else if (level() instanceof ServerLevel serverLevel) {
            spawnAtLocation(serverLevel, getItem());
        }
    }

    private void dropAsItem() {
        if (!level().isClientSide()) {
            ItemStack stack = getItem().copy();
            if (stack.isDamageableItem()) {
                int newDamage = stack.getDamageValue() + 1;
                if (newDamage < stack.getMaxDamage()) {
                    stack.setDamageValue(newDamage);
                    spawnAtLocation((ServerLevel) level(), stack);
                } else {
                    playSound(SoundEvents.ITEM_BREAK.value(), 0.8F, 0.8F + level().getRandom().nextFloat() * 0.4F);
                }
            } else {
                spawnAtLocation((ServerLevel) level(), stack);
            }
        }
        discard();
    }

    private void harvestCrops(Vec3 start, Vec3 motion) {
        var length = motion.length();
        if (length < 1.0E-4D) return;

        Vec3 direction = motion.scale(1.0D / length);
        Set<BlockPos> broken = new HashSet<>();

        for (var distance = 0.0D; distance <= length; distance += 0.3D) {
            var pos = BlockPos.containing(start.add(direction.scale(distance)));

            if (!broken.add(pos)) continue;

            BlockState state = level().getBlockState(pos);
            if (state.is(BlockTags.CROPS)) level().destroyBlock(pos, true, this);
        }
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        if (entity.isSpectator() || !entity.isAlive() || !entity.isPickable()) {
            return false;
        }
        Entity owner = getOwner();
        if (entity == owner) {
            return false;
        }
        return !this.hitEntities.contains(entity.getUUID());
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        Entity target = hitResult.getEntity();
        if (target == getOwner() || this.hitEntities.contains(target.getUUID())) {
            return;
        }

        this.hitEntities.add(target.getUUID());
        Entity owner = getOwner();
        DamageSource damageSource = damageSources().thrown(this, owner);
        target.hurt(damageSource, this.getTier().getDamage());
        playSound(SoundEvents.TRIDENT_HIT, 1.0F, 1.0F);
        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.CRIT, target.getX(), target.getY(0.5), target.getZ(), 12, 0.25, 0.25, 0.25, 0.15);
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult hitResult) {
        if (isReturning()) {
            return;
        }

        BlockPos pos = hitResult.getBlockPos();
        BlockState state = level().getBlockState(pos);

        super.onHitBlock(hitResult);

        if (state.is(Blocks.DECORATED_POT)) {
            level().destroyBlock(pos, true, this);
            Vec3 end = position().add(getDeltaMovement());
            setPos(end.x, end.y, end.z);
            return;
        }

        Direction face = hitResult.getDirection();
        Vec3 normal = new Vec3(face.getStepX(), face.getStepY(), face.getStepZ());
        Vec3 motion = getDeltaMovement();

        double dot = motion.dot(normal);
        if (dot < 0.0) {
            Vec3 reflected = motion.subtract(normal.scale(2.0 * dot));
            setPos(hitResult.getLocation().add(normal.scale(0.05)));
            setDeltaMovement(reflected);
            playSound(SoundEvents.TRIDENT_HIT_GROUND, 0.9F, 1.2F);

            this.bounces++;
            this.distanceTraveled = 0.0;
            if (this.bounces >= this.getTier().getMaxBounces()) {
                if (this.entityData.get(ID_DISPENSED)) {
                    dropAsItem();
                    return;
                }
                setReturning(true);
            }
        } else {
            Vec3 end = position().add(getDeltaMovement());
            setPos(end.x, end.y, end.z);
        }
    }

    @Override
    public void remove(Entity.RemovalReason reason) {
        ACTIVE_DISCS.remove(this);
        super.remove(reason);
    }

    public static void recoverForPlayer(ServerPlayer player) {
        UUID playerId = player.getUUID();
        for (ThrowingDiscEntity disc : Set.copyOf(ACTIVE_DISCS)) {
            if (!disc.isRemoved() && playerId.equals(disc.ownerUUID)) {
                if (!player.getAbilities().instabuild) {
                    ItemStack returnStack = disc.getItem();
                    if (!player.getInventory().add(returnStack)) {
                        player.drop(returnStack, false);
                    }
                }
                disc.discard();
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("FlightTicks", this.flightTicks);
        output.putInt("Bounces", this.bounces);
        output.putDouble("DistanceTraveled", this.distanceTraveled);
        output.putBoolean("Returning", isReturning());
        output.putBoolean("Dispensed", this.entityData.get(ID_DISPENSED));
        output.putInt("ThrownHand", this.thrownHand.ordinal());
        if (this.ownerUUID != null) {
            output.putString("OwnerUUID", this.ownerUUID.toString());
        }
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.flightTicks = input.getIntOr("FlightTicks", 0);
        this.bounces = input.getIntOr("Bounces", 0);
        this.distanceTraveled = input.getDoubleOr("DistanceTraveled", 0.0);
        setReturning(input.getBooleanOr("Returning", false));
        this.entityData.set(ID_DISPENSED, input.getBooleanOr("Dispensed", false));
        int handIdx = input.getIntOr("ThrownHand", 0);
        this.thrownHand = handIdx >= 0 && handIdx < InteractionHand.values().length ? InteractionHand.values()[handIdx] : InteractionHand.MAIN_HAND;
        input.getString("OwnerUUID").ifPresent(uuidStr -> {
            try {
                this.ownerUUID = UUID.fromString(uuidStr);
            } catch (Exception ignored) {
            }
        });
    }
}
