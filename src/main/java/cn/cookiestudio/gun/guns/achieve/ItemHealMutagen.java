package cn.cookiestudio.gun.guns.achieve;

import cn.cookiestudio.gun.guns.ItemHealBase;
import cn.nukkit.Player;
import cn.nukkit.potion.Effect;

public class ItemHealMutagen extends ItemHealBase {
    public ItemHealMutagen() {
        super("myname:mutagen", "Мутаген", "mutagen", 1);
    }

    @Override
    public void interact(Player player) {
        player.addEffect(Effect.getEffect(Effect.REGENERATION).setAmplifier(2).setDuration(260));

        super.removeCustomItem(player);
        super.sendCooldown(player);
    }
}
