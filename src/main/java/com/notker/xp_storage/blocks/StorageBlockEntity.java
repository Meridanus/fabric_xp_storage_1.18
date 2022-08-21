package com.notker.xp_storage.blocks;

import com.notker.xp_storage.XpStorage;
import com.notker.xp_storage.regestry.ModBlocks;
import com.notker.xp_storage.regestry.ModFluids;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

import static com.notker.xp_storage.blocks.StorageBlock.CHARGED;

@SuppressWarnings("UnstableApiUsage")
public class StorageBlockEntity extends BlockEntity {

    public UUID player_uuid = Util.NIL_UUID;
    public Text playerName = Text.of("");
    public boolean vacuum = false;
    public boolean isAuthPlayer = false;

    public StorageBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.STORAGE_BLOCK_ENTITY, pos, state);
    }

    public final SingleVariantStorage<FluidVariant> liquidXp = new SingleVariantStorage<>() {
        @Override
        protected FluidVariant getBlankVariant() {
            return FluidVariant.blank();
        }

        @Override
        protected long getCapacity(FluidVariant variant) {
            return FluidConstants.BUCKET * Integer.MAX_VALUE;
        }


        @Override
        public long insert(FluidVariant insertedVariant, long maxAmount, TransactionContext transaction) {
            StoragePreconditions.notBlankNotNegative(insertedVariant, maxAmount);
            if (canInsert(insertedVariant)) {
                long insertedAmount = Math.min(maxAmount, getCapacity(insertedVariant) - amount);

                if (insertedAmount > 0) {
                    updateSnapshots(transaction);

                    if (variant.isBlank()) {
                        variant = FluidVariant.of(ModFluids.LIQUID_XP);
                        amount = insertedAmount;
                    } else {
                        amount += insertedAmount;
                    }
                }
                return insertedAmount;

            }

            return 0;
        }

        @Override
        protected boolean canInsert(FluidVariant variant) {
            var variant_name = variant.getFluid().getDefaultState().getBlockState().toString();
            return variant_name.contains("liquid_xp") || variant_name.contains("xp_fluid");
        }

        @Override
        protected boolean canExtract(FluidVariant variant) {
            var variant_name = variant.getFluid().getDefaultState().getBlockState().toString();
            boolean isUnlocked = player_uuid.equals(Util.NIL_UUID) || isAuthPlayer;
            return isUnlocked && (variant_name.contains("liquid_xp") || variant_name.contains("xp_fluid"));
        }

        @Override
        protected void onFinalCommit() {
            if (world != null) {
                world.setBlockState(pos, getCachedState().with(CHARGED, liquidXp.amount != 0));
            }
            isAuthPlayer = false;
            markDirty();
            toUpdatePacket();
        }
    };

    public void setUuidAndNameTo() {
        setUuidAndNameTo(Util.NIL_UUID, Text.of(""));
    }

    public void setUuidAndNameTo(UUID id, Text name) {
        this.player_uuid = id;
        this.playerName = name;
        this.markDirty();
        this.toUpdatePacket();
    }


    public String getContainerFillPercentage() {
        float container_progress = (float) ((100.0 / this.liquidXp.getCapacity()) * this.liquidXp.amount);
        return String.format(java.util.Locale.US,"%.7f", container_progress) + "%";
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        tag.put("fluidVariant", this.liquidXp.variant.toNbt());
        tag.putLong("amount", this.liquidXp.amount);
        tag.putUuid("player_uuid", this.player_uuid);
        tag.putString("playerName", this.playerName.toString());
        tag.putBoolean("vacuum", this.vacuum);
        writeIdToNbt(tag, ModBlocks.STORAGE_BLOCK_ENTITY);

        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), net.minecraft.block.Block.NOTIFY_ALL);
        }
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        if (tag.contains("containerExperience")) {
            this.liquidXp.amount = (long)tag.getInt("containerExperience") * XpStorage.MB_PER_XP;
            this.liquidXp.variant = FluidVariant.of(ModFluids.LIQUID_XP);
        } else {
            this.liquidXp.variant = FluidVariant.fromNbt(tag.getCompound("fluidVariant"));
            this.liquidXp.amount = tag.getLong("amount");
        }


        this.player_uuid = tag.getUuid("player_uuid");
        this.playerName = Text.of(tag.getString("playerName"));
        this.vacuum = tag.getBoolean("vacuum");
    }

    public NbtCompound getNbtData() {
        NbtCompound stackTag = new NbtCompound();
        stackTag.putUuid("player_uuid", this.player_uuid);
        stackTag.putString("playerName", this.playerName.toString());
        stackTag.put("fluidVariant", this.liquidXp.variant.toNbt());
        stackTag.putLong("amount", this.liquidXp.amount);
        writeIdToNbt(stackTag, ModBlocks.STORAGE_BLOCK_ENTITY);
        stackTag.putBoolean("vacuum", this.vacuum);
        return stackTag;
    }

    public void toggleVacuum() {
        this.vacuum = !this.vacuum;
        this.toUpdatePacket();
    }

    public int getContainerExperience() {
        return (int)(this.liquidXp.amount / XpStorage.MB_PER_XP);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return this.createNbt();
    }




        public static void tick(World world, BlockPos pos, BlockState state, StorageBlockEntity sbe) {
            if (sbe.vacuum && world != null) {

                Vec3d center = new Vec3d(pos.getX(), pos.getY() + 2.5D, pos.getZ());
                List<ExperienceOrbEntity> validEntitys = world.getEntitiesByClass(ExperienceOrbEntity.class, Box.of(center, 9D, 5D , 9D), EntityPredicates.VALID_ENTITY);


                validEntitys.forEach(experienceOrbEntity -> {
                    int xp = experienceOrbEntity.getExperienceAmount();
                    if (xp > 0) {
                        try (Transaction transaction = Transaction.openOuter()) {
                            sbe.liquidXp.insert(FluidVariant.of(ModFluids.LIQUID_XP), (long) xp * XpStorage.MB_PER_XP, transaction);
                            transaction.commit();
                        }
                    }
                    experienceOrbEntity.discard();
                });
                world.setBlockState(pos, state.with(CHARGED, (sbe.liquidXp.amount != 0)));
            }

        }

}
