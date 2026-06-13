package com.smd.ticlib.stats;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants;
import slimeknights.tconstruct.library.modifiers.ModifierNBT;

import java.util.ArrayList;
import java.util.List;

public final class TicStatPatches {

    static final String TAG_BONUSES = "Bonuses";
    static final String TAG_TOKENS = "Tokens";

    private TicStatPatches() {
    }

    public static String[] getNumericStatKeys(NBTTagCompound stats) {
        if (stats == null || stats.isEmpty()) {
            return new String[0];
        }
        List<String> keys = new ArrayList<>();
        for (String key : stats.getKeySet()) {
            if (isNumeric(stats.getTag(key))) {
                keys.add(key);
            }
        }
        return keys.toArray(new String[0]);
    }

    public static boolean hasNumericStat(NBTTagCompound stats, String statName) {
        return stats != null && isValidStatName(statName) && isNumeric(stats.getTag(statName));
    }

    static boolean addBonus(NBTTagCompound modifierTag, String identifier, int color, String statName, double amount, String token) {
        if (!isValidStatName(statName) || token == null || token.trim().isEmpty() || hasToken(modifierTag, token)) {
            return false;
        }
        NBTTagCompound bonuses = modifierTag.getCompoundTag(TAG_BONUSES);
        bonuses.setDouble(statName, bonuses.getDouble(statName) + amount);
        modifierTag.setTag(TAG_BONUSES, bonuses);
        addToken(modifierTag, token);
        writeBaseModifierData(modifierTag, identifier, color);
        return true;
    }

    static boolean hasToken(NBTTagCompound modifierTag, String token) {
        if (modifierTag == null || token == null || token.trim().isEmpty()) {
            return false;
        }
        NBTTagList tokens = modifierTag.getTagList(TAG_TOKENS, Constants.NBT.TAG_STRING);
        for (int i = 0; i < tokens.tagCount(); i++) {
            if (token.equals(tokens.getStringTagAt(i))) {
                return true;
            }
        }
        return false;
    }

    static void applyBonuses(NBTTagCompound stats, NBTTagCompound modifierTag) {
        if (stats == null || modifierTag == null || !modifierTag.hasKey(TAG_BONUSES, Constants.NBT.TAG_COMPOUND)) {
            return;
        }
        NBTTagCompound bonuses = modifierTag.getCompoundTag(TAG_BONUSES);
        for (String statName : bonuses.getKeySet()) {
            if (!hasNumericStat(stats, statName)) {
                continue;
            }
            addNumeric(stats, statName, bonuses.getDouble(statName));
        }
    }

    private static void addToken(NBTTagCompound modifierTag, String token) {
        NBTTagList tokens = modifierTag.getTagList(TAG_TOKENS, Constants.NBT.TAG_STRING);
        tokens.appendTag(new NBTTagString(token));
        modifierTag.setTag(TAG_TOKENS, tokens);
    }

    private static void writeBaseModifierData(NBTTagCompound modifierTag, String identifier, int color) {
        ModifierNBT data = ModifierNBT.readTag(modifierTag);
        data.identifier = identifier;
        data.color = color;
        if (data.level <= 0) {
            data.level = 1;
        }
        data.write(modifierTag);
    }

    private static void addNumeric(NBTTagCompound tag, String key, double amount) {
        NBTBase value = tag.getTag(key);
        if (value == null) {
            return;
        }
        switch (value.getId()) {
            case Constants.NBT.TAG_BYTE:
                tag.setByte(key, (byte) (tag.getByte(key) + (int) amount));
                break;
            case Constants.NBT.TAG_SHORT:
                tag.setShort(key, (short) (tag.getShort(key) + (int) amount));
                break;
            case Constants.NBT.TAG_INT:
                tag.setInteger(key, tag.getInteger(key) + (int) amount);
                break;
            case Constants.NBT.TAG_LONG:
                tag.setLong(key, tag.getLong(key) + (long) amount);
                break;
            case Constants.NBT.TAG_FLOAT:
                tag.setFloat(key, tag.getFloat(key) + (float) amount);
                break;
            case Constants.NBT.TAG_DOUBLE:
                tag.setDouble(key, tag.getDouble(key) + amount);
                break;
            default:
                break;
        }
    }

    private static boolean isNumeric(NBTBase value) {
        if (value == null) {
            return false;
        }
        switch (value.getId()) {
            case Constants.NBT.TAG_BYTE:
            case Constants.NBT.TAG_SHORT:
            case Constants.NBT.TAG_INT:
            case Constants.NBT.TAG_LONG:
            case Constants.NBT.TAG_FLOAT:
            case Constants.NBT.TAG_DOUBLE:
                return true;
            default:
                return false;
        }
    }

    private static boolean isValidStatName(String statName) {
        return statName != null && !statName.trim().isEmpty();
    }
}
