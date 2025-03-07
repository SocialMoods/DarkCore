package cn.cookiestudio.gun.guns;

import cn.cookiestudio.gun.GunPlugin;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.item.Item;
import cn.nukkit.scheduler.NukkitRunnable;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FiringTask extends NukkitRunnable {

    private final Map<Player, Boolean> firing = new ConcurrentHashMap<>();

    public FiringTask() {
        Server.getInstance().getScheduler().scheduleRepeatingTask(GunPlugin.getInstance(), this, 1);
    }

    public void startFire(Player player) {
        firing.put(player, true);
    }

    public void stopFire(Player player) {
        firing.remove(player);
    }

    @Override
    public void run() {
        for (Player player : firing.keySet()) {
            if (player.getInventory() != null) {
                Item item = player.getInventory().getItemInHandFast();
                if (item.getId() != Item.AIR) {
                    if (item instanceof ItemGunBase base) {
                        base.interact(player);
                    } else {
                        stopFire(player);
                    }
                } else {
                    stopFire(player);
                }
            } else {
                stopFire(player);
            }
        }
    }

    public boolean isFiring(Player player) {
        return firing.containsKey(player);
    }
}
