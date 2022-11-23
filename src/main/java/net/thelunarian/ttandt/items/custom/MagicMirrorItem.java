package net.thelunarian.ttandt.items.custom;

import java.util.Optional;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static net.thelunarian.ttandt.TerrariaTrinketsAndThings.getConfig;

public class MagicMirrorItem extends Item{

    public MagicMirrorItem(Settings settings) {
        super(settings);
    }

    // private void teleportToSpawn(MinecraftServer server, ServerPlayerEntity serverUser, Hand hand){
    //     ServerWorld overWorld = server.getOverworld();
    //     BlockPos defaultSpawnPoint = overWorld.getSpawnPos();
    //     serverUser.teleport(overWorld ,defaultSpawnPoint.getX(), defaultSpawnPoint.getY(), defaultSpawnPoint.getZ(), 0, 0);
    //     overWorld.playSound( null, serverUser.getPos().x,serverUser.getPos().y,serverUser.getPos().z, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.NEUTRAL, 1, 1);
    //     overWorld.sendEntityStatus(serverUser, (byte)46);
    //     serverUser.getStackInHand(hand).damage(1, serverUser, p -> p.sendToolBreakStatus(hand));
    // }

    // @Override
    // public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
    //     ItemStack itemStack = user.getStackInHand(hand);
    //     if (!world.isClient) {
    //         MinecraftServer server = world.getServer();
    //         ServerPlayerEntity serverUser = server.getPlayerManager().getPlayer(user.getUuid());

    //         ServerWorld dimension = server.getWorld(serverUser.getSpawnPointDimension());
    //         BlockPos pos = serverUser.getSpawnPointPosition();

    //         world.playSound( null, user.getPos().x,user.getPos().y,user.getPos().z, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.NEUTRAL, 1f, 1f);
    //         world.sendEntityStatus(user, (byte)46);
    //         user.getItemCooldownManager().set(this, 30);

    //         if(dimension !=null && pos !=null){
    //             Optional<Vec3d> userSpawn = PlayerEntity.findRespawnPosition(dimension, pos, 0f, false, user.isAlive());
    //             if(userSpawn.isPresent()){
    //                 serverUser.teleport(dimension, userSpawn.get().x, userSpawn.get().y, userSpawn.get().z, 0f, 0f);
    //                 dimension.playSound( null, serverUser.getPos().x,serverUser.getPos().y,serverUser.getPos().z, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.NEUTRAL, 1f, 1f);
    //                 dimension.sendEntityStatus(serverUser, (byte)46);
    //                 serverUser.getStackInHand(hand).damage(1, serverUser, p -> p.sendToolBreakStatus(hand));
    //             }
    //             else{
    //                 teleportToSpawn(server, serverUser, hand);
    //             }
    //         }
    //         else{
    //             teleportToSpawn(server,serverUser, hand);
    //         }
    //     }
        
    //     return TypedActionResult.success(itemStack, world.isClient());
    // }





    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return Ingredient.ofItems(Items.DIAMOND).test(ingredient) || super.canRepair(stack, ingredient);
    }

    @Override
    public int getEnchantability() {
        return 5;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }
    
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return ItemUsage.consumeHeldItem(world, user, hand);
    }

    private final int useTime = getConfig().useTime;

    public int getMaxUseTime(ItemStack stack) {
        return useTime;
    }

    public void teleportFail(World world, PlayerEntity player, String cause) {
        world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_NOTE_BLOCK_CHIME, SoundCategory.BLOCKS, 1f, 1f);
        switch(cause) {
            case "xp": player.sendMessage(Text.translatable("terrariatrinketsandthings.xpfail")); break;
            case "dimensionlock": player.sendMessage(Text.translatable("terrariatrinketsandthings.dimfail")); break;
            case "nospawnset" : player.sendMessage(Text.translatable("terrariatrinketsandthings.spawnfail")); break;
            default: ; break;
        }
    }
    
    public void costExp(PlayerEntity player, ServerPlayerEntity serverPlayer) {
        if (!player.getAbilities().creativeMode) {
            serverPlayer.setExperienceLevel(player.experienceLevel - getConfig().xpCost);
        }
    }

    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        PlayerEntity player = (PlayerEntity)user;
        if (!world.isClient()) {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity)player;
            ServerWorld serverWorld = serverPlayer.server.getWorld(serverPlayer.getSpawnPointDimension());
            //player.sendMessage(Text.literal("spawn :" + serverPlayer.getSpawnPointDimension() + serverPlayer.getSpawnPointPosition()));
            if (player.experienceLevel >= getConfig().xpCost || player.getAbilities().creativeMode) { //if enough xp or creative
                if (serverWorld != null && (getConfig().interdimensional || serverWorld == serverPlayer.getWorld().toServerWorld())) {
                    //player.sendMessage(Text.literal("debug string C"));
                    BlockPos spawnpoint = serverPlayer.getSpawnPointPosition();
                    if (spawnpoint != null) {
                        //player.sendMessage(Text.literal("debug string D"));
                        Optional<Vec3d> optionalSpawnVec = PlayerEntity.findRespawnPosition(serverWorld, spawnpoint, serverPlayer.getSpawnAngle(), false, false);
                        //player.sendMessage(Text.literal("optionalSpawnVec:" + optionalSpawnVec));
                        //Player spawn
                        BlockPos finalSpawnpoint = spawnpoint;
                        // optionalSpawnVec.ifPresent(spawnVec -> {
                        //     serverPlayer.teleport(serverWorld, spawnVec.getX(), spawnVec.getY(), spawnVec.getZ(), serverPlayer.getSpawnAngle(), 0.5F);
                        //     serverWorld.playSound(null, finalSpawnpoint, SoundEvents.BLOCK_PORTAL_TRAVEL, SoundCategory.PLAYERS, 0.4f, 1f);
                        //     serverWorld.sendEntityStatus(serverPlayer, (byte)46);
                        // });
                        if(optionalSpawnVec.isPresent()){
                            serverPlayer.teleport(serverWorld, optionalSpawnVec.get().x, optionalSpawnVec.get().y, optionalSpawnVec.get().z, serverPlayer.getSpawnAngle(), 0.5F);
                            serverWorld.playSound(null, finalSpawnpoint, SoundEvents.BLOCK_PORTAL_TRAVEL, SoundCategory.PLAYERS, 0.4f, 1f);
                            serverWorld.sendEntityStatus(serverPlayer, (byte)46);
                            costExp(player, serverPlayer);
                            stack.damage(1, serverPlayer, p -> p.sendToolBreakStatus(Hand.MAIN_HAND));
                        } else if (!getConfig().spawnSet) {
                            //player.sendMessage(Text.literal("debug string A"));
                            spawnpoint = serverWorld.getServer().getOverworld().getSpawnPos();
                            serverPlayer.teleport(serverWorld.getServer().getOverworld(), spawnpoint.getX(), spawnpoint.getY(), spawnpoint.getZ(), serverPlayer.getSpawnAngle(), 0.5F);
                            while (!serverWorld.getServer().getOverworld().isSpaceEmpty(serverPlayer)) {
                                serverPlayer.teleport(serverPlayer.getX(), serverPlayer.getY() + 1.0D, serverPlayer.getZ());
                            }
                            serverWorld.playSound(null, spawnpoint, SoundEvents.BLOCK_PORTAL_TRAVEL, SoundCategory.PLAYERS, 0.4f, 1f);
                            serverWorld.sendEntityStatus(serverPlayer, (byte)46);
                            costExp(player, serverPlayer);
                            stack.damage(1, serverPlayer, p -> p.sendToolBreakStatus(Hand.MAIN_HAND));
                        } else {
                            teleportFail(world, player, "nospawnset");
                        }

                    } else {
                        // World Spawn
                        if (!getConfig().spawnSet) {
                            //player.sendMessage(Text.literal("debug string A'"));
                            spawnpoint = serverWorld.getSpawnPos();
                            serverPlayer.teleport(serverWorld, spawnpoint.getX(), spawnpoint.getY(), spawnpoint.getZ(), serverPlayer.getSpawnAngle(), 0.5F);
                            while (!serverWorld.isSpaceEmpty(serverPlayer)) {
                                serverPlayer.teleport(serverPlayer.getX(), serverPlayer.getY() + 1.0D, serverPlayer.getZ());
                            }
                            serverWorld.playSound(null, spawnpoint, SoundEvents.BLOCK_PORTAL_TRAVEL, SoundCategory.PLAYERS, 0.4f, 1f);
                            serverWorld.sendEntityStatus(serverPlayer, (byte)46);
                            costExp(player, serverPlayer);
                        } else { 
                            teleportFail(world, player, "nospawnset");
                        }
                    }
                } else {
                    //player.sendMessage(Text.literal("debug string B"));
                    teleportFail(world, player, "dimensionlock");
                }
            } else {  //if not enough xp
                teleportFail(world, player, "xp");
            }
        }
        if (player != null) {
            player.getItemCooldownManager().set(this, getConfig().cooldown);
            player.incrementStat(Stats.USED.getOrCreateStat(this));
        }
        return stack;
    }
}







    // @Override
    // public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
    //     PlayerEntity player = (PlayerEntity)user;
    //     if (!world.isClient()) {
    //         ServerPlayerEntity serverUser = (ServerPlayerEntity)player;
    //         MinecraftServer server = world.getServer();

    //         ServerWorld dimension = server.getWorld(serverUser.getSpawnPointDimension());
    //         BlockPos pos = serverUser.getSpawnPointPosition();

    //         world.playSound( null, user.getPos().x,user.getPos().y,user.getPos().z, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.NEUTRAL, 1f, 1f);
    //         world.sendEntityStatus(user, (byte)46);
    //         player.getItemCooldownManager().set(this, 30);

    //         if(dimension !=null && pos !=null){
    //             Optional<Vec3d> userSpawn = PlayerEntity.findRespawnPosition(dimension, pos, 0f, false, user.isAlive());
    //             if(userSpawn.isPresent()){
    //                 serverUser.teleport(dimension, userSpawn.get().x, userSpawn.get().y, userSpawn.get().z, 0f, 0f);
    //                 dimension.playSound( null, serverUser.getPos().x,serverUser.getPos().y,serverUser.getPos().z, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.NEUTRAL, 1f, 1f);
    //                 dimension.sendEntityStatus(serverUser, (byte)46);
    //                 stack.damage(1, serverUser, p -> p.sendToolBreakStatus(hand));
    //             }
    //             else{
    //                 teleportToSpawn(server, serverUser, hand);
    //             }
    //         }
    //         else{
    //             teleportToSpawn(server,serverUser, hand);
    //         }
    //     }
        
    //     return TypedActionResult.success(stack, world.isClient());
    // }

    





    // @Override
    // public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
    //     if (!world.isClient() && hand == Hand.MAIN_HAND && user instanceof ServerPlayerEntity) {
    //         user.sendMessage(Text.literal("spawn :" + ((ServerPlayerEntity) user).getSpawnPointPosition()));
    //     }
    //     return super.use(world, user, hand);
    // }

