package cn.cookiestudio.gun.guns.achieve;

import cn.cookiestudio.gun.guns.ItemHealBase;
import cn.nukkit.Player;
import cn.nukkit.potion.Effect;

public class ItemHealAdrenaline extends ItemHealBase {
   public ItemHealAdrenaline() {
      super("cole:cadrenaline", "Адреналин", "cadrenaline", 1);
   }

   public void interact(Player player) {
      player.addEffect(Effect.getEffect(1).setAmplifier(3).setDuration(200));
      player.addEffect(Effect.getEffect(11).setAmplifier(2).setDuration(200));
      super.interact(player);
   }
}
