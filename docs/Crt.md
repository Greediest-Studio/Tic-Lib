# TicLib CraftTweaker / Java 用法

`TicLib` 是一个轻量的 `TiC / ConArm` 工具库。

- Java 侧可直接依赖 `com.smd.ticlib.util.*`
- CraftTweaker 侧统一入口为 `mods.ticlib.TicTool`
- 本库只处理 `TiC / ConArm` 工具、护甲、注册 trait 与 `Stats` 数值字段

## NBT 写入语义

`TicLib` 的属性写入现在走持久化隐藏 modifier：

- `addStat` / `addIntStat` 会把增量保存到隐藏 modifier 的 NBT 中。
- 工具使用 `ticlib_stats`，护甲使用 ConArm 实际 modifier id `ticlib_stats_armor`。
- 经过工具装配台、部件替换或 rebuild 时，隐藏 modifier 会重新把保存的增量应用到当前 `Stats`。
- 只支持 `Stats` 下已经存在的数值字段；不会创建未知字段，也不会模拟其它 mod 的特殊 modifier 行为。

注册 trait 方法仍走原生注册路径：

- `applyRegisteredTrait` / `removeRegisteredTrait` 只处理已注册到 `TinkerRegistry` 的 trait。
- 会同步 `Traits`、`Modifiers`、`Base.Modifiers`。

## 查询

```zenscript
mods.ticlib.TicTool.isTool(stack as IItemStack) as bool
mods.ticlib.TicTool.isArmor(stack as IItemStack) as bool
mods.ticlib.TicTool.getAllItems() as IItemStack[]
mods.ticlib.TicTool.getArmorType(stack as IItemStack) as string
mods.ticlib.TicTool.getArmorSlot(stack as IItemStack) as IEntityEquipmentSlot
mods.ticlib.TicTool.getMaterials(stack as IItemStack) as string[]
```

- `getAllItems` 动态返回 `TinkerRegistry.getTools()` 与 `ArmoryRegistry.getArmor()` 中的物品。
- `getArmorSlot` 对非护甲返回 `null`。

## Stats 数值字段

```zenscript
mods.ticlib.TicTool.getStats(stack as IItemStack) as string[]
mods.ticlib.TicTool.hasStat(stack as IItemStack, statName as string) as bool
mods.ticlib.TicTool.getFloatStat(stack as IItemStack, statName as string) as float
mods.ticlib.TicTool.getIntStat(stack as IItemStack, statName as string) as int
mods.ticlib.TicTool.addStat(stack as IItemStack, statName as string, amount as float, token as string) as bool
mods.ticlib.TicTool.addIntStat(stack as IItemStack, statName as string, amount as int, token as string) as bool
```

说明：

- `getStats` 返回当前 `Stats` 下所有数值字段名，包含 byte、short、int、long、float、double。
- `addStat` 用于浮点或通用数值字段。
- `addIntStat` 用于整数字段；内部仍保存为持久化增量。
- 相同物品上相同 `token` 只会应用一次。
- `statName` 必须已经存在于当前物品 `Stats` 中。

示例：

```zenscript
import mods.ticlib.TicTool;

for stat in TicTool.getStats(stack) {
    print(stat);
}

TicTool.addStat(stack, "MiningSpeed", 2.0, "bonus_speed");
TicTool.addIntStat(stack, "FreeModifiers", 1, "bonus_slot");

// 其它 mod 写入 Stats 的数值字段也可以用字符串操作
TicTool.addStat(stack, "MagicBookRange", 5.0, "bonus_magic_range");
TicTool.addStat(stack, "MagicBookCritChance", 0.15, "bonus_magic_crit");
TicTool.addIntStat(stack, "MagicBookSpellSpeed", 1, "bonus_spell_speed");
```

## 词条

```zenscript
mods.ticlib.TicTool.getTraits(stack as IItemStack) as string[]
mods.ticlib.TicTool.hasTrait(stack as IItemStack, traitId as string) as bool
mods.ticlib.TicTool.getTraitColor(stack as IItemStack, traitId as string) as int
mods.ticlib.TicTool.getTraitLevel(stack as IItemStack, traitId as string) as int
mods.ticlib.TicTool.applyRegisteredTrait(stack as IItemStack, traitId as string, color as int, level as int) as bool
mods.ticlib.TicTool.removeRegisteredTrait(stack as IItemStack, traitId as string) as bool
mods.ticlib.TicTool.withRegisteredTrait(stack as IItemStack, traitId as string, color as int, level as int) as IItemStack
mods.ticlib.TicTool.withoutRegisteredTrait(stack as IItemStack, traitId as string) as IItemStack
```

兼容别名：

```zenscript
mods.ticlib.TicTool.addTrait(stack as IItemStack, traitId as string, color as int, level as int) as bool
mods.ticlib.TicTool.removeTrait(stack as IItemStack, traitId as string) as bool
mods.ticlib.TicTool.withTrait(stack as IItemStack, traitId as string, color as int, level as int) as IItemStack
mods.ticlib.TicTool.withoutTrait(stack as IItemStack, traitId as string) as IItemStack
```

## 其它修改

```zenscript
mods.ticlib.TicTool.setBroken(stack as IItemStack, broken as bool) as bool
```

`setBroken` 直接设置 `Stats.Broken`。

## 护甲缓存

```zenscript
mods.ticlib.TicTool.getArmorTraits(player as IPlayer) as string[]
mods.ticlib.TicTool.getArmorSlotTraits(player as IPlayer, slotName as string) as string[]
mods.ticlib.TicTool.hasArmorTrait(player as IPlayer, traitId as string) as bool
mods.ticlib.TicTool.hasArmorSlotTrait(player as IPlayer, slotName as string, traitId as string) as bool
mods.ticlib.TicTool.refreshArmorCache(player as IPlayer) as bool
```

槽位名支持：

- `head` / `helmet`
- `chest` / `chestplate`
- `legs` / `leggings`
- `feet` / `boots`

## Java 示例

```java
import com.smd.ticlib.util.TicArmorTraitCache;
import com.smd.ticlib.util.TicToolStacks;
import com.smd.ticlib.util.TicToolStats;
import com.smd.ticlib.util.TicToolTraits;

if (TicToolStacks.isTicTool(stack)) {
    String[] materials = TicToolStacks.getMaterials(stack);
    String[] stats = TicToolStats.getStats(stack);
}

TicToolStats.addStat(stack, "MagicBookRange", 5.0F, "example_range");
TicToolTraits.applyRegisteredTrait(stack, "sharp", 0xffffff, 1);

String[] armorTraits = TicArmorTraitCache.INSTANCE.getArmorTraits(player);
```
