package cn.cookiestudio.gun.guns.achieve;

import cn.cookiestudio.gun.guns.ItemHealBase;
import cn.nukkit.Player;

public class ItemHealAptechka extends ItemHealBase {
   public ItemHealAptechka() {
      super("cole:captechka", "Аптечка", "captechka", 11);
   }

   public void interact(Player player) {
      if (player.getHealth() != player.getMaxHealth()) {
         super.interact(player);
      }

   }

   public int getMaxStackSize() {
      return 8;
   }
}
