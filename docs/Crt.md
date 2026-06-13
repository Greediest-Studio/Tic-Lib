# TicLib CraftTweaker / Java 用法

`TicLib` 是一个轻量的 `TiC / ConArm` 工具库。

- Java 侧可直接依赖 `com.smd.ticlib.util.*`
- CraftTweaker 侧统一入口为 `mods.ticlib.TicTool`
- CraftTweaker 构建事件入口为 `mods.ticlib.TicEvents`
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

构建事件里的 trait 方法是 build 专用语义：

- `event.addTrait` / `event.applyRegisteredTrait` 只修改当前 build 过程中的 `Traits` 与 `Modifiers`。
- 不写入 `Base.Modifiers`，适合按材料、工具类型或其它条件附加“常态词条”。
- 下次工具装配台 rebuild 时会重新触发事件并重新计算。
- `event.addBaseModifier` / `event.removeBaseModifier` 只直接修改 `Base.Modifiers` 字符串列表。
- `Base.Modifiers` 会被 TiC / ConArm 原生 rebuild 重放。因为构建事件发生在原生 modifier 重放前，本次 build 的最终结果通常也会带上新写入的 base modifier。

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

## 构建事件

```zenscript
import mods.ticlib.TicEvents;

TicEvents.onToolBuild(function(event as mods.ticlib.event.ToolBuildEvent) {
    if (event.toolId == "tconstruct:pickaxe" && event.hasMaterial("manyullyn")) {
        event.addTrait("sharp", 0xffffff, 1);
    }

    if (event.hasMaterial("cobalt")) {
        event.addBaseModifier("haste");
    }

    if (event.hasStat("MiningSpeed")) {
        event.addStat("MiningSpeed", 1.0);
    }
});

TicEvents.onArmorBuild(function(event as mods.ticlib.event.ArmorBuildEvent) {
    if (event.hasMaterial("cobalt")) {
        event.addTrait("speedy_armor", 0xffffff, 1);
    }
});
```

事件对象：

```zenscript
event.itemId as string
event.toolId as string
event.armorId as string
event.materials as string[]
event.hasMaterial(materialId as string) as bool

event.getTraits() as string[]
event.hasTrait(traitId as string) as bool
event.addTrait(traitId as string, color as int, level as int) as bool
event.applyRegisteredTrait(traitId as string, color as int, level as int) as bool
event.removeTrait(traitId as string) as bool
event.getBaseModifiers() as string[]
event.hasBaseModifier(traitOrModifierId as string) as bool
event.addBaseModifier(traitOrModifierId as string) as bool
event.removeBaseModifier(traitOrModifierId as string) as bool

event.getStats() as string[]
event.hasStat(statName as string) as bool
event.getFloatStat(statName as string) as float
event.getIntStat(statName as string) as int
event.addStat(statName as string, amount as float) as bool
event.addIntStat(statName as string, amount as int) as bool
```

说明：

- `onToolBuild` 对应 TiC `TinkerEvent.OnItemBuilding`。
- `onArmorBuild` 对应 ConArm `ArmoryEvent.OnItemBuilding`。
- 事件会在工具/护甲构建和 rebuild 时触发，此时没有完整 `IItemStack`，所以事件方法直接操作原生 root NBT。
- `event.addTrait` 是“本次 build 注入”：立即把已注册 trait 应用到当前 `Traits` / `Modifiers`，本轮构建结果马上带有该词条；不会写入 `Base.Modifiers`。
- `event.addTrait` 适合按材料、工具类型、护甲类型动态计算出来的常态词条；条件变化后，下次 rebuild 会按脚本重新计算。
- `event.addBaseModifier` 是“只直接写 Base.Modifiers”：只把解析后的 modifier id 写进原生持久 modifier 列表，本方法本身不直接改 `Traits` / `Modifiers` / `Stats`。
- `event.addBaseModifier` 可传 trait id 或 modifier id；如果传 trait id，会自动解析成对应 modifier id。
- `event.addBaseModifier` 的后果是该 modifier 会在本次事件之后的原生 modifier 重放阶段和后续 rebuild 中被持续重放；后续条件不满足时不会自动消失，除非脚本主动 `removeBaseModifier`。
- 如果需要“条件不满足就消失”的动态词条，请使用 `event.addTrait`。
- 不建议对同一个 trait 同时调用 `event.addTrait` 和 `event.addBaseModifier`，除非你明确希望当前 build 和后续 Base 重放都参与。
- `event.addStat` 是本次 build 的即时 `Stats` 调整；需要长期保存的后续变化仍用 `TicTool.addStat(stack, statName, amount, token)`。

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
