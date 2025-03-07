package cn.cookiestudio.gun.guns;

import cn.nukkit.item.customitem.CustomItemDefinition;
import cn.nukkit.item.customitem.ItemCustom;
import cn.nukkit.item.customitem.data.ItemCreativeCategory;
import cn.nukkit.item.customitem.data.ItemCreativeGroup;
import cn.nukkit.item.customitem.data.RenderOffsets;

public class ItemKnifeBase extends ItemCustom {
   private final int damage;

   public ItemKnifeBase(String namespaceId, String name, String textureName, int damage) {
      super(namespaceId, name, textureName);
      this.damage = damage;
   }

   public boolean isSword() {
      return true;
   }

   public int getAttackDamage() {
      return this.damage;
   }

   public int getMaxStackSize() {
      return 1;
   }

   public CustomItemDefinition getDefinition() {
      return CustomItemDefinition.simpleBuilder(this, ItemCreativeCategory.EQUIPMENT).creativeGroup(ItemCreativeGroup.SWORD).allowOffHand(false).handEquipped(false).renderOffsets(RenderOffsets.scaleOffset(16)).build();
   }
}
