package cn.cookiestudio.gun.guns.achieve;

import cn.cookiestudio.gun.guns.ItemHealBase;
import cn.nukkit.Player;
import cn.nukkit.potion.Effect;

public class ItemHealMorphine extends ItemHealBase {
   public ItemHealMorphine() {
      super("cole:cmorfin", "Морфин", "cmorfin", 7);
   }

   @Override
   public void interact(Player player) {
      if (player.getHealth() != player.getMaxHealth()) {
         player.addEffect(Effect.getEffect(Effect.NAUSEA).setAmplifier(0).setDuration(150));
         player.setHealth(player.getMaxHealth());

         super.interact(player);
      }
   }
}
