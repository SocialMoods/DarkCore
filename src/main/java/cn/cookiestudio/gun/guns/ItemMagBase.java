package cn.cookiestudio.gun.guns;

import cn.nukkit.item.customitem.CustomItemDefinition;
import cn.nukkit.item.customitem.ItemCustom;
import cn.nukkit.item.customitem.data.ItemCreativeCategory;
import cn.nukkit.item.customitem.data.ItemCreativeGroup;
import cn.nukkit.item.customitem.data.RenderOffsets;

public abstract class ItemMagBase extends ItemCustom {
   public ItemMagBase(String namespaceId, String itemName, String textureName) {
      super(namespaceId, itemName, textureName);
   }

   public abstract int getSkinId();

   public abstract float getDropItemScale();

   public int getMaxStackSize() {
      return 16;
   }

   public CustomItemDefinition getDefinition() {
      return CustomItemDefinition.simpleBuilder(this, ItemCreativeCategory.ITEMS).creativeGroup(ItemCreativeGroup.NONE).allowOffHand(false).handEquipped(false).renderOffsets(RenderOffsets.scaleOffset(16)).build();
   }
}
