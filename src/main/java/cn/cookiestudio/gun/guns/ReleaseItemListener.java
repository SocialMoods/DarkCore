package cn.cookiestudio.gun.guns;

import cn.cookiestudio.gun.GunPlugin;
import cn.cookiestudio.gun.guns.achieve.ItemGunAwp;
import cn.cookiestudio.gun.guns.achieve.ItemGunBarrett;
import cn.cookiestudio.gun.guns.achieve.ItemGunM3;
import cn.cookiestudio.gun.guns.achieve.ItemGunTaurus;
import cn.cookiestudio.gun.playersetting.PlayerSettingMap;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerAnimationEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerItemHeldEvent;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.inventory.transaction.InventoryTransaction;
import cn.nukkit.inventory.transaction.data.ReleaseItemData;
import cn.nukkit.inventory.transaction.data.UseItemData;
import cn.nukkit.network.protocol.AnimateEntityPacket;
import cn.nukkit.network.protocol.AnimatePacket;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.InventoryTransactionPacket;
import cn.nukkit.Player;
import cn.nukkit.potion.Effect;

import javax.xml.crypto.Data;

public class ReleaseItemListener implements Listener {

    public ReleaseItemListener() {
        Server.getInstance().getPluginManager().registerEvents(this, GunPlugin.getInstance());
    }

    @EventHandler
    public void onPlayerAnimation(PlayerAnimationEvent event) {
        if (event.getAnimationType() == AnimatePacket.Action.SWING_ARM && event.getPlayer().getInventory().getItemInHand() instanceof ItemGunBase) {
            ((ItemGunBase) event.getPlayer().getInventory().getItemInHand()).reload(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getPlayer().getInventory().getItemInHand() instanceof ItemHealBase itemGun && (event.getAction() == PlayerInteractEvent.Action.RIGHT_CLICK_AIR || event.getAction() == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)) {
            itemGun.interact(event.getPlayer());
        }

        if (event.getPlayer().getInventory().getItemInHand() instanceof ItemGunBase gunBase && (event.getAction() == PlayerInteractEvent.Action.RIGHT_CLICK_AIR || event.getAction() == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)) {
            if (gunBase instanceof ItemGunAwp || gunBase instanceof ItemGunM3 || gunBase instanceof ItemGunTaurus || gunBase instanceof ItemGunBarrett) {
                gunBase.interact(event.getPlayer());
            } else {
                if (GunPlugin.getInstance().getTask().isFiring(event.getPlayer())) {
                    GunPlugin.getInstance().getTask().stopFire(event.getPlayer());
                } else {
                    GunPlugin.getInstance().getTask().startFire(event.getPlayer());
                }
            }
        }
    }

    @EventHandler
    public void onPlayerHeldItem(PlayerItemHeldEvent event) {
        if (!(event.getItem() instanceof ItemGunBase)) event.getPlayer().removeEffect(Effect.SLOWNESS);
    }
}
