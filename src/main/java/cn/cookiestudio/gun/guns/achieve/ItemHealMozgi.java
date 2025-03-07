package cn.cookiestudio.gun.guns.achieve;

import cn.cookiestudio.gun.guns.ItemHealBase;
import cn.nukkit.Player;

public class ItemHealMozgi extends ItemHealBase {
   public ItemHealMozgi() {
      super("cole:mozg", "Мозги", "mozg", 11);
   }

   public void interact(Player player) {
      if (player.getHealth() != player.getMaxHealth()) {
         super.interact(player);
      }

   }
}
