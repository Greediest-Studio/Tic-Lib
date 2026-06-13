package com.smd.ticlib.stats;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import slimeknights.tconstruct.library.modifiers.IModifier;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.TinkerUtil;

final class TicStatModifierBridge {

    private TicStatModifierBridge() {
    }

    static boolean addStat(ItemStack stack, IModifier modifier, String identifier, int color, String statName, double amount, String token) {
        if (stack == null || stack.isEmpty() || token == null || token.trim().isEmpty()) {
            return false;
        }

        NBTTagCompound root = TagUtil.getTagSafe(stack).copy();
        NBTTagCompound stats = TagUtil.getToolTag(root);
        if (!TicStatPatches.hasNumericStat(stats, statName)) {
            return false;
        }

        NBTTagList modifiers = TagUtil.getModifiersTagList(root);
        int index = TinkerUtil.getIndexInCompoundList(modifiers, identifier);
        NBTTagCompound modifierTag = index >= 0 ? modifiers.getCompoundTagAt(index).copy() : new NBTTagCompound();
        if (TicStatPatches.hasToken(modifierTag, token)) {
            return true;
        }
        if (!TicStatPatches.addBonus(modifierTag, identifier, color, statName, amount, token)) {
            return false;
        }
        if (index >= 0) {
            modifiers.set(index, modifierTag);
        } else {
            modifiers.appendTag(modifierTag);
        }
        TagUtil.setModifiersTagList(root, modifiers);
        modifier.apply(root);
        stack.setTagCompound(root);
        return true;
    }
}
