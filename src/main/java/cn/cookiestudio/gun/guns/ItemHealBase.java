package cn.cookiestudio.gun.guns;

import cn.cookiestudio.gun.guns.achieve.ItemHealAdrenaline;
import cn.cookiestudio.gun.guns.achieve.ItemHealMorphine;
import cn.nukkit.Player;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.inventory.PlayerUIInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemEnderPearl;
import cn.nukkit.item.customitem.CustomItemDefinition;
import cn.nukkit.item.customitem.ItemCustom;
import cn.nukkit.item.customitem.data.ItemCreativeCategory;
import cn.nukkit.item.customitem.data.ItemCreativeGroup;
import cn.nukkit.item.customitem.data.RenderOffsets;
import cn.nukkit.network.protocol.PlayerStartItemCoolDownPacket;
import cn.nukkit.network.protocol.ProtocolInfo;

import java.util.Map;

public class ItemHealBase extends ItemCustom {
   private final int healCoefficient;

   public ItemHealBase(String namespaceId, String name, String textureName, int healCoefficient) {
      super(namespaceId, name, textureName);
      this.healCoefficient = healCoefficient;
   }

   public int getMaxStackSize() {
      return 16;
   }

   public CustomItemDefinition getDefinition() {
      return CustomItemDefinition.simpleBuilder(this, ItemCreativeCategory.ITEMS).creativeGroup(ItemCreativeGroup.NONE).allowOffHand(false).handEquipped(false).renderOffsets(RenderOffsets.scaleOffset(16)).build();
   }

   public void interact(Player player) {
      if (player.getHealth() != player.getMaxHealth() && !(this instanceof ItemHealAdrenaline) && !(this instanceof ItemHealMorphine)) {
         player.setHealth(player.getHealth() + (float)this.healCoefficient);
      }

      this.removeCustomItem(player);
   }

   public void sendCooldown(Player player) {
      PlayerStartItemCoolDownPacket pk = new PlayerStartItemCoolDownPacket();
      pk.setCoolDownDuration(100);

      player.dataPacket(pk);
   }

   public void removeCustomItem(Player player) {
      PlayerInventory inventory = player.getInventory();

      int count = this.getCount() - 1;

      if (count == 0) {
         inventory.clear(inventory.getHeldItemIndex(), true);
         return;
      }

      this.setCount(count);

      inventory.setItemInHand(this);
      inventory.sendContents(player);
   }
}
