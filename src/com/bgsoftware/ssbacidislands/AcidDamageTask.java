package com.bgsoftware.ssbacidislands;

import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class AcidDamageTask extends BukkitRunnable {

    private static final SSBAcidIslands plugin = SSBAcidIslands.getPlugin();
    private static final Map<UUID, AcidDamageTask> acidDamageTasks = new HashMap<>();

    private final Player player;
    private double lastDamage = plugin.getSettings().firstDamage;

    private AcidDamageTask(Player player){
        this.player = player;
        runTaskTimer(plugin, 20L, 20L);
    }

    @Override
    public void run() {
        if(!player.isOnline()) {
            stopTask(player);
            return;
        }

        GameMode gameMode = player.getGameMode();

        if(gameMode == GameMode.CREATIVE || gameMode == GameMode.SPECTATOR){
            stopTask(player);
            return;
        }

        Block playerBlock = player.getLocation().getBlock();

        if(!playerBlock.getType().name().contains("WATER") && !playerBlock.getRelative(BlockFace.DOWN).getType().name().contains("WATER")){
            stopTask(player);
            return;
        }

        player.damage(lastDamage);
        lastDamage *= plugin.getSettings().damageMultiplier;
    }

    public static Optional<AcidDamageTask> getTask(Player player){
        return Optional.ofNullable(acidDamageTasks.get(player.getUniqueId()));
    }

    public static void stopTask(Player player){
        AcidDamageTask acidDamageTask = acidDamageTasks.remove(player.getUniqueId());
        if(acidDamageTask != null)
            acidDamageTask.cancel();
    }

    public static AcidDamageTask createTask(Player player){
        return acidDamageTasks.computeIfAbsent(player.getUniqueId(), u -> new AcidDamageTask(player));
    }

}
