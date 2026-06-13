package com.smd.ticlib.stats;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.modifiers.ModifierTrait;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.utils.TagUtil;

public final class TicToolStatModifier extends ModifierTrait {

    static final String IDENTIFIER = "ticlib_stats";
    private static final int COLOR = 0xffffff;

    public static final TicToolStatModifier INSTANCE = new TicToolStatModifier();

    private TicToolStatModifier() {
        super(IDENTIFIER, COLOR);
        aspects.clear();
        addAspects(new ModifierAspect.DataAspect(this, COLOR));
    }

    @Override
    public boolean isHidden() {
        return true;
    }

    @Override
    public boolean canApplyCustom(ItemStack stack) {
        return stack != null && !stack.isEmpty() && stack.getItem() instanceof ToolCore;
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
