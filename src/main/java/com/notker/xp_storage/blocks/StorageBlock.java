package com.notker.xp_storage.blocks;

import com.notker.xp_storage.XpFunctions;
import com.notker.xp_storage.fluids.Xp_fluid;
import com.notker.xp_storage.items.Xp_removerItem;
import com.notker.xp_storage.regestry.ModBlocks;
import com.notker.xp_storage.regestry.ModFluids;
import com.notker.xp_storage.regestry.ModItems;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Objects;


public class StorageBlock extends HorizontalFacingBlock implements BlockEntityProvider, Waterloggable {
    public static final BooleanProperty CHARGED = BooleanProperty.of("charged");

    public StorageBlock(Settings settings) {
        super(settings.nonOpaque());
        setDefaultState(getStateManager().getDefaultState()
                .with(Properties.HORIZONTAL_FACING, Direction.NORTH)
                .with(CHARGED, false)
                .with(Properties.WATERLOGGED, false));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        boolean bl = fluidState.getFluid() == Fluids.WATER;
        return this.getDefaultState().with(Properties.HORIZONTAL_FACING, ctx.getPlayerFacing().getOpposite()).with(Properties.WATERLOGGED, bl);
    }

    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(Properties.WATERLOGGED)) {
            //world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
            world.createAndScheduleFluidTick(pos, Fluids.WATER,Fluids.WATER.getTickRate(world));
        }

        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    public FluidState getFluidState(BlockState state) {
        return state.get(Properties.WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    public boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
        if (!(Boolean)state.get(Properties.WATERLOGGED) && fluidState.getFluid() == Fluids.WATER) {
            BlockState blockState = state.with(Properties.WATERLOGGED, true);

                world.setBlockState(pos, blockState, 3);


            //world.getFluidTickScheduler().schedule(pos, fluidState.getFluid(), fluidState.getFluid().getTickRate(world));
            world.createAndScheduleFluidTick(pos, fluidState.getFluid(), fluidState.getFluid().getTickRate(world));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext ctx) {

        VoxelShape bottom = Block.createCuboidShape(1D, 0D, 1D, 15D, 2D, 15D);
        VoxelShape top = Block.createCuboidShape(2D, 2D, 2D, 14D, 14D, 14D);

        return VoxelShapes.union(bottom, top);

    }


    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        final StorageBlockEntity tile = (StorageBlockEntity) world.getBlockEntity(pos);
        if (tile == null) { return; }

        ItemStack drop = new ItemStack(asItem());

        if (tile.containerExperience > 0 || !tile.player_uuid.equals(Util.NIL_UUID)) {
            NbtCompound stackTag = new NbtCompound();
            //stackTag.put(ModBlocks.TAG_ID, tile.writeNbt(new NbtCompound()));


            stackTag.put(ModBlocks.TAG_ID, tile.getNbtData());
            drop.setNbt(stackTag);
            //data get entity @s SelectedItem
        }

        dropStack(world,pos,drop);

        super.onBreak(world, pos, state, player);
    }


    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(Properties.HORIZONTAL_FACING);
        stateManager.add(CHARGED);
        stateManager.add(Properties.WATERLOGGED);
    }


    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        final StorageBlockEntity tile = (StorageBlockEntity) world.getBlockEntity(pos);
        if (tile == null) { return; }

        if (tile.containerExperience != 0) {
            world.setBlockState(pos, state.with(CHARGED, true));
        }
        super.onPlaced(world, pos, state, placer, itemStack);
    }



    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            boolean isSurvival = !player.isCreative() && !player.isSpectator();

            int itemCountInHand = player.getStackInHand(hand).getCount();
            final StorageBlockEntity tile = (StorageBlockEntity) world.getBlockEntity(pos);

            if (tile == null) {
                return ActionResult.FAIL;
            }

            boolean tileIsLocked = !tile.player_uuid.equals(Util.NIL_UUID);
            boolean isTileOwner = tile.player_uuid.equals(player.getUuid());




            // Inspector
            if (player.isHolding(ModItems.INSPECTOR)) {
                world.playSound(null, pos, SoundEvents.ITEM_BOOK_PUT, SoundCategory.BLOCKS, 0.5f, 1f);
                display_container_info(player, tile);
                return ActionResult.SUCCESS;

            }

            // Lock
            if (player.isHolding(ModItems.LOCK)) {
                if (!tileIsLocked) {
                    lockContainer(world, pos, player, hand, isSurvival, itemCountInHand, tile);
                } else if (isTileOwner) {
                    player.sendMessage(new TranslatableText("text.storageBlock.isLocked"), true);
                    world.playSound(null, pos, SoundEvents.BLOCK_SCULK_SENSOR_CLICKING, SoundCategory.BLOCKS, 1f, 1f);
                }
                return ActionResult.SUCCESS;
            }

            // Key
            if (player.isHolding(ModItems.KEY)) {
                if (isTileOwner || player.isCreative()) {
                    unlock_container(world, pos, player, hand, isSurvival, itemCountInHand, tile);
                }
                return ActionResult.SUCCESS;
            }


            if (tileIsLocked && !isTileOwner) {
                world.playSound(null, pos, SoundEvents.BLOCK_SCULK_SENSOR_CLICKING, SoundCategory.BLOCKS, 1f, 1f);
                player.sendMessage(new TranslatableText("text.storageBlock.denied"), true);
                return ActionResult.SUCCESS;
            }
            // Ab hier nur für TileOwner
            if (isSurvival) {
                // XP Tool
                if (player.isHolding(ModItems.XP_REMOVER)) {
                    ItemStack stack = player.getStackInHand(hand);

                    if (stack.hasNbt() && Objects.requireNonNull(stack.getNbt()).getBoolean(Xp_removerItem.tagId)) {
                        container_xp_to_player(itemCountInHand, state, world, pos, player, tile);
                    } else {
                        player_xp_to_container(itemCountInHand, state, world, pos, player, tile);
                    }
                    tile.markDirty();
                    tile.toUpdatePacket();
                    return ActionResult.SUCCESS;
                }

                // EP Flasche
                if (player.isHolding(Items.EXPERIENCE_BOTTLE)) {
                    insertBottleXP(itemCountInHand, state, world, pos, player, hand, tile);
                    return ActionResult.SUCCESS;
                }

                // Empty Bucket
                if (player.isHolding(Items.BUCKET) && tile.containerExperience >= Xp_fluid.XpPerBucket) {
                    fillBucketOnContainer(world, pos, player, hand, tile);
                    return ActionResult.SUCCESS;
                }

                // Experience Bucket
                if (player.isHolding(ModFluids.XP_BUCKET)) {
                    emptyBucketOnContainer(world, pos, player, hand, tile);
                    return ActionResult.SUCCESS;
                }

            }


        }

        return ActionResult.CONSUME;
    }

    private void emptyBucketOnContainer(World world, BlockPos pos, PlayerEntity player, Hand hand, StorageBlockEntity tile) {
        tile.addXpToContainer(Xp_fluid.XpPerBucket);

        player.getStackInHand(hand).setCount(0);
        player.getInventory().offerOrDrop(new ItemStack(Items.BUCKET));

        world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1f, 1f);
    }

    private void fillBucketOnContainer(World world, BlockPos pos, PlayerEntity player, Hand hand, StorageBlockEntity tile) {
        tile.containerExperience -= Xp_fluid.XpPerBucket;
        tile.markDirty();
        tile.toUpdatePacket();

        player.getStackInHand(hand).decrement(1);
        player.getInventory().offerOrDrop(new ItemStack(ModFluids.XP_BUCKET));

        world.playSound(null, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1f, 1f);
    }


    private void insertBottleXP(int itemCount, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, StorageBlockEntity tile) {
        ItemStack emptyBottles = new ItemStack(Items.GLASS_BOTTLE);
        emptyBottles.setCount(itemCount);

        int xpToInsert = 0;

        for (int i = 0; i < itemCount; i++) {
            // Minecraft EP Calculation
            xpToInsert += 3 + world.random.nextInt(5) + world.random.nextInt(5);
        }

        tile.addXpToContainer(xpToInsert);

        world.setBlockState(pos, state.with(CHARGED, (tile.containerExperience != 0)));
        world.playSound(null, pos, SoundEvents.BLOCK_BREWING_STAND_BREW, SoundCategory.BLOCKS, 1f, 1f);

        player.getStackInHand(hand).setCount(0);

        dropStack(world, pos, player.getHorizontalFacing().getOpposite(), emptyBottles);
        //player.getInventory().offerOrDrop(emptyBottle);
    }

    private void container_xp_to_player (int itemCount, BlockState state, World world, BlockPos pos, PlayerEntity player, StorageBlockEntity tile) {
        if (tile.containerExperience > 0) {

            for (int i = 0; i < itemCount; i++) {
                int xpToNextLevel = XpFunctions.exp_to_reach_next_lvl(player.getNextLevelExperience(), player.experienceProgress);
                int storageXP = tile.containerExperience;

                if (storageXP == 0) {
                    break;
                }

                if (storageXP >= xpToNextLevel) {
                    tile.containerExperience -= xpToNextLevel;
                    player.addExperience(xpToNextLevel);
                } else {
                    player.addExperience(storageXP);
                    tile.containerExperience = 0;
                }
                tile.markDirty();
                tile.toUpdatePacket();
            }

            world.playSound(null, pos, SoundEvents.BLOCK_RESPAWN_ANCHOR_DEPLETE, SoundCategory.BLOCKS, 0.125f, 1f);
            world.setBlockState(pos, state.with(CHARGED, (tile.containerExperience != 0)));
        }
    }

    private void player_xp_to_container (int itemCount, BlockState state, World world, BlockPos pos, PlayerEntity player, StorageBlockEntity tile) {
        if (XpFunctions.get_total_xp(player.experienceLevel, player.getNextLevelExperience(), player.experienceProgress) > 0) {

            int xpToInsert = 0;

            for (int i = 0; i < itemCount; i++) {
                int totalPlayerXp = XpFunctions.get_total_xp(player.experienceLevel, player.getNextLevelExperience(), player.experienceProgress);
                int xpExchange = XpFunctions.xp_value_from_bar(player.getNextLevelExperience(), player.experienceProgress);
                if (xpExchange == 0) {
                    xpExchange = XpFunctions.getToNextLowerExperienceLevel(player.experienceLevel);
                    player.experienceProgress = 0f;
                }

                if (totalPlayerXp >= xpExchange) {
                    player.addExperience(-xpExchange);
                    xpToInsert += xpExchange;
                } else {
                    player.addExperience(-totalPlayerXp);
                    xpToInsert += totalPlayerXp;
                }
            }

            tile.addXpToContainer(xpToInsert);

            world.playSound(null, pos, SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE, SoundCategory.BLOCKS, 0.125f, 1f);
            world.setBlockState(pos, state.with(CHARGED, (tile.containerExperience != 0)));
        }
    }


    private void unlock_container(World world, BlockPos pos, PlayerEntity player, Hand hand, boolean isSurvival, int itemCountInHand, StorageBlockEntity tile) {
        tile.setUuidAndNameTo();

        player.sendMessage(new TranslatableText("text.storageBlock.isOpen"), true);
        world.playSound(null, pos, SoundEvents.BLOCK_SMITHING_TABLE_USE, SoundCategory.BLOCKS, 1f, 1f);

        if (isSurvival) {
            player.getStackInHand(hand).setCount(itemCountInHand > 1 ? itemCountInHand - 1 : 0);
        }
    }

    private void lockContainer(World world, BlockPos pos, PlayerEntity player, Hand hand, boolean isSurvival, int itemCountInHand, StorageBlockEntity tile) {
        tile.setUuidAndNameTo(player.getUuid(), player.getName());

        player.sendMessage(new TranslatableText("text.storageBlock.locked"), true);
        world.playSound(null, pos, SoundEvents.BLOCK_SMITHING_TABLE_USE, SoundCategory.BLOCKS, 1f, 1f);

        if (isSurvival) {
            player.getStackInHand(hand).setCount(itemCountInHand > 1 ? itemCountInHand - 1 : 0);
        }
    }

    private void display_container_info (PlayerEntity player, StorageBlockEntity tile) {
        int totalXp = XpFunctions.get_total_xp(player.experienceLevel, player.getNextLevelExperience(), player.experienceProgress);

        if (!tile.player_uuid.equals(Util.NIL_UUID)) {
            player.sendSystemMessage(new TranslatableText("item.tooltip.owner", tile.playerName.asString()), Util.NIL_UUID);
            player.sendSystemMessage(new LiteralText("UUid: " + tile.player_uuid), Util.NIL_UUID);
        } else {
            player.sendSystemMessage(new TranslatableText("item.debug_info.xp.container_no_owner"), Util.NIL_UUID);
        }
        String xp = String.format(Locale.GERMAN, "%,d", tile.containerExperience);
        String max = String.format(Locale.GERMAN, "%,d", Integer.MAX_VALUE);
        String playerXp = String.format(Locale.GERMAN,"%,d", totalXp);

        player.sendSystemMessage(new TranslatableText("item.debug_info.xp.container_info", xp, max), Util.NIL_UUID);
        player.sendSystemMessage(new TranslatableText("item.debug_info.xp.container_fill", tile.getContainerFillPercentage()), Util.NIL_UUID);
        player.sendSystemMessage(new TranslatableText("item.debug_info.xp.player_info", playerXp), Util.NIL_UUID);
    }


    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new StorageBlockEntity(pos, state);
    }


}
