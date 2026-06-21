package com.smd.ticlib.module.stats;

import com.smd.ticlib.core.data.TicDataAccess;
import com.smd.ticlib.core.lifecycle.TicLifecycleContext;
import com.smd.ticlib.core.lifecycle.TicModule;
import com.smd.ticlib.core.nbt.TicNbt;
import com.smd.ticlib.core.target.TicTarget;
import com.smd.ticlib.core.target.TicTargetKind;
import com.smd.ticlib.core.target.TicTargets;
import com.smd.ticlib.core.tconstruct.TicNativeAccess;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants;

public final class PersistentStatsModule implements TicModule {

    public static final PersistentStatsModule INSTANCE = new PersistentStatsModule();
    public static final String ID = "ticlib:stats";

    private static final String BONUSES = "Bonuses";
    private static final String TOKENS = "Tokens";

    private PersistentStatsModule() {
    }

    @Override
    public String id() {
        return ID;
    }

    @Override
    public boolean supports(TicTargetKind kind) {
        return kind == TicTargetKind.TOOL || kind == TicTargetKind.ARMOR;
    }

    @Override
    public void onBuild(TicLifecycleContext context) {
        apply(context);
    }

    @Override
    public void onReplay(TicLifecycleContext context) {
        apply(context);
    }

    public String[] getStats(ItemStack stack) {
        TicTarget target = TicTargets.resolve(stack);
        if (!target.isValid()) {
            return TicNbt.EMPTY_STRINGS;
        }
        return target.nativeAccess().getStatNames();
    }

    public boolean hasStat(ItemStack stack, String statName) {
        TicTarget target = TicTargets.resolve(stack);
        return target.isValid() && target.nativeAccess().hasNumericStat(statName);
    }

    public float getFloat(ItemStack stack, String statName) {
        return hasStat(stack, statName) ? TicTargets.resolve(stack).nativeAccess().stats().getFloat(statName) : 0.0F;
    }

    public int getInt(ItemStack stack, String statName) {
        return hasStat(stack, statName) ? TicTargets.resolve(stack).nativeAccess().stats().getInteger(statName) : 0;
    }

    public boolean add(ItemStack stack, String statName, double amount, String token) {
        TicTarget target = TicTargets.resolve(stack);
        if (!target.isValid() || token == null || token.trim().isEmpty()) {
            return false;
        }
        TicNativeAccess nativeAccess = target.nativeAccess();
        if (!nativeAccess.hasNumericStat(statName)) {
            return false;
        }
        TicDataAccess data = target.data();
        NBTTagCompound component = data.writableComponent(ID);
        if (hasToken(component, token)) {
            data.commit();
            return true;
        }

        NBTTagCompound bonuses = component.getCompoundTag(BONUSES);
        bonuses.setDouble(statName, bonuses.getDouble(statName) + amount);
        component.setTag(BONUSES, bonuses);
        addToken(component, token);

        nativeAccess.addNumericStat(statName, amount);
        data.markDirty();
        data.commit();
        return true;
    }

    public boolean apply(TicLifecycleContext context) {
        if (context == null) {
            return false;
        }
        NBTTagCompound component = context.data().component(ID);
        if (component == null || !component.hasKey(BONUSES, Constants.NBT.TAG_COMPOUND)) {
            return false;
        }
        TicNativeAccess nativeAccess = context.nativeAccess();
        NBTTagCompound stats = nativeAccess.stats().copy();
        NBTTagCompound bonuses = component.getCompoundTag(BONUSES);
        boolean changed = false;
        for (String statName : bonuses.getKeySet()) {
            changed |= TicNbt.addNumeric(stats, statName, bonuses.getDouble(statName));
        }
        if (changed) {
            nativeAccess.setStats(stats);
        }
        return changed;
    }

    private static boolean hasToken(NBTTagCompound component, String token) {
        NBTTagList tokens = component.getTagList(TOKENS, Constants.NBT.TAG_STRING);
        for (int i = 0; i < tokens.tagCount(); i++) {
            if (token.equals(tokens.getStringTagAt(i))) {
                return true;
            }
        }
        return false;
    }

    private static void addToken(NBTTagCompound component, String token) {
        NBTTagList tokens = component.getTagList(TOKENS, Constants.NBT.TAG_STRING);
        tokens.appendTag(new NBTTagString(token));
        component.setTag(TOKENS, tokens);
    }
}
