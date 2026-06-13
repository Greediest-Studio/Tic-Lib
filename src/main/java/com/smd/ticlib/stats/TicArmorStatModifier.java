package com.smd.ticlib.stats;

import c4.conarm.lib.armor.ArmorCore;
import c4.conarm.lib.modifiers.ArmorModifierTrait;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.utils.TagUtil;

public final class TicArmorStatModifier extends ArmorModifierTrait {

    private static final String BASE_IDENTIFIER = "ticlib_stats";
    private static final int COLOR = 0xffffff;

    public static final TicArmorStatModifier INSTANCE = new TicArmorStatModifier();

    private TicArmorStatModifier() {
        super(BASE_IDENTIFIER, COLOR);
        aspects.clear();
        addAspects(new ModifierAspect.DataAspect(this, COLOR));
    }

    @Override
    public boolean isHidden() {
        return true;
    }

    @Override
    public boolean canApplyCustom(ItemStack stack) {
        return stack != null && !stack.isEmpty() && stack.getItem() instanceof ArmorCore;
    }

    @Override
    public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
        NBTTagCompound stats = TagUtil.getToolTag(rootCompound).copy();
        TicStatPatches.applyBonuses(stats, modifierTag);
        TagUtil.setToolTag(rootCompound, stats);
    }

    public boolean addStat(ItemStack stack, String statName, double amount, String token) {
        if (!canApplyCustom(stack)) {
            return false;
        }
        return TicStatModifierBridge.addStat(stack, getModifierIdentifier(), COLOR, statName, amount, token);
    }
}
