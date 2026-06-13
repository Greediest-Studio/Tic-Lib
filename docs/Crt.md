# TicLib CraftTweaker / Java 用法

`TicLib` 是一个轻量的 `TiC / ConArm` 工具库。

- Java 侧可直接依赖 `com.smd.ticlib.util.*`
- CraftTweaker 侧统一入口为 `mods.ticlib.TicTool`
- 本库只提供 `ticlib` 能力，不包含内容型扩展接口

## CraftTweaker

统一入口：

```zenscript
mods.ticlib.TicTool
```

说明：

- 所有方法都以 `mods.ticlib.TicTool` 为根入口
- Zen 层只负责类型转换和空对象保护，实际逻辑在 Java `util` 层
- 非 `TiC` 工具或非 `ConArm` 护甲传入时，大多数查询方法会返回默认值，修改方法会返回 `false`

## 查询方法

### `isTool`

签名：

```zenscript
mods.ticlib.TicTool.isTool(stack as IItemStack) as bool
```

说明：

- 判断物品是否为 `TiC` 工具
- 判定优先基于 `ToolCore` / `ITinkerable` 与原生 TiC 数据

参数：

- `stack`：要判断的物品

返回值：

- `true`：是 `TiC` 工具
- `false`：不是 `TiC` 工具，或者输入为空

### `getAllItems`

签名：

```zenscript
mods.ticlib.TicTool.getAllItems() as IItemStack[]
```

说明：

- 动态返回当前已注册的 `TiC` 工具与 `ConArm` 护甲物品
- 数据来源是 `TinkerRegistry.getTools()` 与 `ArmoryRegistry.getArmor()`

返回值：

- 当前环境下可识别的全部 `TiC / ConArm` 物品实例数组

### `isArmor`

签名：

```zenscript
mods.ticlib.TicTool.isArmor(stack as IItemStack) as bool
```

说明：

- 判断物品是否为 `ConArm` 护甲
- 判定基于 `ArmorCore`

参数：

- `stack`：要判断的物品

返回值：

- `true`：是 `ConArm` 护甲
- `false`：不是，或者输入为空

### `getArmorType`

签名：

```zenscript
mods.ticlib.TicTool.getArmorType(stack as IItemStack) as string
```

说明：

- 返回护甲类型名称
- 仅对 `ConArm` 护甲有效

参数：

- `stack`：要查询的护甲物品

返回值：

- `helmet`
- `chestplate`
- `leggings`
- `boots`
- 非 `ConArm` 护甲时返回空字符串 `""`

### `getArmorSlot`

签名：

```zenscript
mods.ticlib.TicTool.getArmorSlot(stack as IItemStack) as IEntityEquipmentSlot
```

说明：

- 返回护甲实际所属的装备槽位
- 仅对 `ConArm` 护甲有效

参数：

- `stack`：要查询的护甲物品

返回值：

- 对应的 `IEntityEquipmentSlot`
- 非护甲时返回空槽位对象

### `getMaterials`

签名：

```zenscript
mods.ticlib.TicTool.getMaterials(stack as IItemStack) as string[]
```

说明：

- 读取工具或护甲的材料列表
- 数据来源是 `TinkerData.Materials`
- 返回顺序与原始部件顺序一致

参数：

- `stack`：要读取的 `TiC` 工具或 `ConArm` 护甲

返回值：

- 材料标识符数组
- 非目标物品时返回空数组

## 词条方法

### `getTraits`

签名：

```zenscript
mods.ticlib.TicTool.getTraits(stack as IItemStack) as string[]
```

说明：

- 读取物品当前词条列表
- 数据来源是根 NBT 下的 `Traits`
- 保留重复词条，不做去重

参数：

- `stack`：要读取的 `TiC` 工具或 `ConArm` 护甲

返回值：

- 词条标识符数组
- 非目标物品时返回空数组

### `hasTrait`

签名：

```zenscript
mods.ticlib.TicTool.hasTrait(stack as IItemStack, traitId as string) as bool
```

说明：

- 判断物品是否拥有指定词条
- 只要词条列表中出现一次即视为存在

参数：

- `stack`：目标物品
- `traitId`：词条标识符

返回值：

- `true`：存在该词条
- `false`：不存在，输入无效，或物品不适用

### `getTraitColor`

签名：

```zenscript
mods.ticlib.TicTool.getTraitColor(stack as IItemStack, traitId as string) as int
```

说明：

- 查询指定词条对应的颜色值
- 从 `Modifiers` 中查找对应 `identifier`
- 返回值为十六进制颜色整数

参数：

- `stack`：目标物品
- `traitId`：词条标识符

返回值：

- 找到时返回词条颜色
- 找不到时返回 `0xffffff`

### `getTraitLevel`

签名：

```zenscript
mods.ticlib.TicTool.getTraitLevel(stack as IItemStack, traitId as string) as int
```

说明：

- 查询指定词条等级
- 从 `Modifiers` 中查找对应 `identifier`
- 对普通 trait，通常结果是 `1`
- 对分级 trait，会返回合并后的当前等级

参数：

- `stack`：目标物品
- `traitId`：词条标识符

返回值：

- 找到时返回等级
- 找不到时返回 `1`

### `addTrait`

签名：

```zenscript
mods.ticlib.TicTool.addTrait(stack as IItemStack, traitId as string, color as int, level as int) as bool
```

说明：

- 向物品追加一个已注册词条
- 当前实现会先解析 `TinkerRegistry` 中的原生 trait，再按 TiC 标准 trait 应用流程写入
- 会同步处理 `Traits`、`Modifiers`、`BaseModifiers` 对应关系
- 默认拒绝重复添加同名词条

参数：

- `stack`：要修改的物品，必须是可变栈
- `traitId`：词条标识符，必须是已注册 trait
- `color`：写入的显示颜色，通常使用 `0xffffff` 这种 RGB 整数
- `level`：目标等级，普通 trait 一般传 `1`

返回值：

- `true`：添加成功，或已按标准路径完成写入
- `false`：物品不适用、词条不存在、词条重复、NBT 结构不合法，或无法按原生规则应用

注意：

- `addTrait` 只接受已注册的 `TiC / ConArm` trait
- 对某些带特殊内部状态的复杂 trait，仍建议实机过工作台验证

### `removeTrait`

签名：

```zenscript
mods.ticlib.TicTool.removeTrait(stack as IItemStack, traitId as string) as bool
```

说明：

- 从物品中移除指定词条
- 会同步清理 `Traits`
- 若该词条对应的 modifier 已无剩余关联 trait，则会从 `Modifiers` 与 `BaseModifiers` 一并移除
- 若是共享同一 modifier 的分级 trait，则会按剩余 trait 重建 modifier 数据

参数：

- `stack`：要修改的物品，必须是可变栈
- `traitId`：要移除的词条标识符

返回值：

- `true`：移除成功
- `false`：物品不适用、词条不存在，或输入无效

### `withTrait`

签名：

```zenscript
mods.ticlib.TicTool.withTrait(stack as IItemStack, traitId as string, color as int, level as int) as IItemStack
```

说明：

- 基于原物品副本添加词条
- 原物品不会被污染

参数：

- `stack`：原物品
- `traitId`：要追加的词条
- `color`：词条颜色
- `level`：词条等级

返回值：

- 新副本
- 输入为空时返回空物品

### `withoutTrait`

签名：

```zenscript
mods.ticlib.TicTool.withoutTrait(stack as IItemStack, traitId as string) as IItemStack
```

说明：

- 基于原物品副本移除词条
- 原物品不会被污染

参数：

- `stack`：原物品
- `traitId`：要移除的词条

返回值：

- 新副本
- 输入为空时返回空物品

## 属性方法

### `setBroken`

签名：

```zenscript
mods.ticlib.TicTool.setBroken(stack as IItemStack, broken as bool) as bool
```

说明：

- 设置物品是否损坏
- 直接修改 `Stats.Broken`
- 仅对 `TiC / ConArm` 目标物品有效

参数：

- `stack`：目标物品
- `broken`：是否损坏

返回值：

- `true`：设置成功
- `false`：物品不适用

### `addMiningSpeed`

签名：

```zenscript
mods.ticlib.TicTool.addMiningSpeed(stack as IItemStack, amount as float, token as string) as bool
```

说明：

- 增加工具挖掘速度
- 同时更新 `Stats` 与 `StatsOriginal`
- 仅对拥有该字段的 `TiC` 工具有效

参数：

- `stack`：目标工具
- `amount`：要增加的数值
- `token`：防重复叠加标记

返回值：

- `true`：修改成功，或相同 token 已经应用过
- `false`：物品不适用、字段不存在、token 无效

### `addAttack`

签名：

```zenscript
mods.ticlib.TicTool.addAttack(stack as IItemStack, amount as float, token as string) as bool
```

说明：

- 增加工具攻击力
- 同时更新 `Stats` 与 `StatsOriginal`

参数：

- `stack`：目标工具
- `amount`：要增加的攻击力
- `token`：防重复叠加标记

返回值：

- `true`：修改成功，或同 token 已应用
- `false`：物品不适用、字段不存在、token 无效

### `addFreeModifiers`

签名：

```zenscript
mods.ticlib.TicTool.addFreeModifiers(stack as IItemStack, amount as int, token as string) as bool
```

说明：

- 增加可用强化槽数量
- 同时更新 `Stats` 与 `StatsOriginal`

参数：

- `stack`：目标工具
- `amount`：增加值
- `token`：防重复叠加标记

返回值：

- `true`：修改成功，或同 token 已应用
- `false`：物品不适用、字段不存在、token 无效

### `addDefense`

签名：

```zenscript
mods.ticlib.TicTool.addDefense(stack as IItemStack, amount as float, token as string) as bool
```

说明：

- 增加护甲防御值
- 仅对 `ConArm` 护甲有效
- 写入时会按护甲槽位使用原生换算倍率
- 同时更新 `Stats` 与 `StatsOriginal`

参数：

- `stack`：目标护甲
- `amount`：外部语义上的增加量
- `token`：防重复叠加标记

返回值：

- `true`：修改成功，或同 token 已应用
- `false`：不是 `ConArm` 护甲、字段不存在、token 无效

### `addToughness`

签名：

```zenscript
mods.ticlib.TicTool.addToughness(stack as IItemStack, amount as float, token as string) as bool
```

说明：

- 增加韧性
- 对 `ConArm` 护甲时走护甲原生 `ArmorNBT`
- 对工具时走工具属性路径

参数：

- `stack`：目标物品
- `amount`：增加值
- `token`：防重复叠加标记

返回值：

- `true`：修改成功，或同 token 已应用
- `false`：物品不适用、字段不存在、token 无效

### `addHarvestLevel`

签名：

```zenscript
mods.ticlib.TicTool.addHarvestLevel(stack as IItemStack, amount as int, token as string) as bool
```

说明：

- 增加工具采掘等级
- 同时更新 `Stats` 与 `StatsOriginal`

参数：

- `stack`：目标工具
- `amount`：增加值
- `token`：防重复叠加标记

返回值：

- `true`：修改成功，或同 token 已应用
- `false`：物品不适用、字段不存在、token 无效

### `addDrawSpeed`

签名：

```zenscript
mods.ticlib.TicTool.addDrawSpeed(stack as IItemStack, amount as float, token as string) as bool
```

说明：

- 修改拉弓速度
- 当前语义是把原始 `DrawSpeed` 数值减去 `amount`
- 最终结果最小值会钳制到 `0.01`
- 同时更新 `Stats` 与 `StatsOriginal`

参数：

- `stack`：目标远程工具
- `amount`：调整量
- `token`：防重复叠加标记

返回值：

- `true`：修改成功，或同 token 已应用
- `false`：物品不适用、字段不存在、token 无效

### `addAttackSpeedMultiplier`

签名：

```zenscript
mods.ticlib.TicTool.addAttackSpeedMultiplier(stack as IItemStack, amount as float, token as string) as bool
```

说明：

- 增加攻击速度倍率
- 同时更新 `Stats` 与 `StatsOriginal`

参数：

- `stack`：目标工具
- `amount`：增加值
- `token`：防重复叠加标记

返回值：

- `true`：修改成功，或同 token 已应用
- `false`：物品不适用、字段不存在、token 无效

## Token 规则

说明：

- `addMiningSpeed`
- `addAttack`
- `addFreeModifiers`
- `addDefense`
- `addToughness`
- `addHarvestLevel`
- `addDrawSpeed`
- `addAttackSpeedMultiplier`

以上属性方法共用同一套防重复机制。

行为规则：

- 每次调用需要传入一个非空 `token`
- 相同物品上，相同 `token` 只会生效一次
- 重复调用不会再次叠加属性
- 标记存储在物品根 NBT 的 `ticlib.applied_tokens`
- 这部分数据不写入 TiC 原生统计字段

## 护甲缓存方法

### `getArmorTraits`

签名：

```zenscript
mods.ticlib.TicTool.getArmorTraits(player as IPlayer) as string[]
```

说明：

- 读取玩家四个护甲槽中的全部 `ConArm` 词条
- 返回结果是四槽位词条的合并数组
- 保留重复词条
- 查询优先命中运行时缓存

参数：

- `player`：目标玩家

返回值：

- 全部护甲词条数组
- 玩家为空时返回空数组

### `getArmorSlotTraits`

签名：

```zenscript
mods.ticlib.TicTool.getArmorSlotTraits(player as IPlayer, slotName as string) as string[]
```

说明：

- 读取玩家指定护甲槽位的词条列表
- 非 `ConArm` 护甲槽位视为空

参数：

- `player`：目标玩家
- `slotName`：槽位名称

支持的 `slotName`：

- `head`
- `helmet`
- `chest`
- `chestplate`
- `legs`
- `leggings`
- `feet`
- `boots`

返回值：

- 对应槽位词条数组
- 槽位名无效或玩家为空时返回空数组

### `hasArmorTrait`

签名：

```zenscript
mods.ticlib.TicTool.hasArmorTrait(player as IPlayer, traitId as string) as bool
```

说明：

- 判断玩家整套护甲是否拥有指定词条
- 查询走缓存中的总合集集合

参数：

- `player`：目标玩家
- `traitId`：词条标识符

返回值：

- `true`：任意护甲槽中存在该词条
- `false`：不存在，或输入无效

### `hasArmorSlotTrait`

签名：

```zenscript
mods.ticlib.TicTool.hasArmorSlotTrait(player as IPlayer, slotName as string, traitId as string) as bool
```

说明：

- 判断指定护甲槽位是否拥有某个词条

参数：

- `player`：目标玩家
- `slotName`：槽位名
- `traitId`：词条标识符

返回值：

- `true`：该槽位存在该词条
- `false`：不存在、槽位无效、输入无效

### `refreshArmorCache`

签名：

```zenscript
mods.ticlib.TicTool.refreshArmorCache(player as IPlayer) as bool
```

说明：

- 手动刷新玩家护甲词条缓存
- 可在自定义事件或特殊时机主动补刷

参数：

- `player`：目标玩家

返回值：

- `true`：刷新成功
- `false`：玩家为空

缓存规则：

- 玩家登录时自动构建
- 护甲槽变化时自动刷新
- 玩家克隆后自动重建
- 查询时若缓存缺失，会自动懒构建

## 示例

```zenscript
import mods.ticlib.TicTool;

if (TicTool.isTool(stack)) {
    print(TicTool.getMaterials(stack));
}

if (TicTool.hasArmorTrait(player, "speed")) {
    print("player has speed");
}

TicTool.addMiningSpeed(stack, 2.0, "example_speed_bonus");
TicTool.addFreeModifiers(stack, 1, "example_modifier_bonus");

val copy = TicTool.withTrait(<tconstruct:pickaxe>, "sharp", 0xff0000, 1);
```

## Java

推荐直接调用 `util` 包：

- `TicToolStacks`
- `TicToolTraits`
- `TicToolStats`
- `TicArmorTraitCache`
- `TicToolNbt`

示例：

```java
import com.smd.ticlib.util.TicArmorTraitCache;
import com.smd.ticlib.util.TicToolStacks;
import com.smd.ticlib.util.TicToolStats;
import com.smd.ticlib.util.TicToolTraits;

if (TicToolStacks.isTicTool(stack)) {
    String[] materials = TicToolStacks.getMaterials(stack);
}

TicToolTraits.addTrait(stack, "sharp", 0xff0000, 1);
TicToolStats.addMiningSpeed(stack, 2.0F, "example_token");

String[] armorTraits = TicArmorTraitCache.INSTANCE.getArmorTraits(player);
```

## 设计说明

- `util` 层是本 mod 作为前置库时的主要 Java API
- `integration.crafttweaker.TicTool` 只是对 `util` 的薄包装
- 判断逻辑优先使用 `TiC / ConArm` 原生类型，而不是旧脚本白名单
- 词条写入已经尽量对齐 TiC / ConArm 原生注册与重建逻辑
