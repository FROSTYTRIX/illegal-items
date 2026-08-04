# Illegal Items

A Fabric mod for **Minecraft 1.21.5** by **FROSTYTRIX**.

Brings Bedrock Edition's illegal and unobtainable content over to Java Edition: the command-only
blocks that were never meant to be held, the items that never left the debug menu, and the entities
that never made the jump between editions. Plus a handful of other oddities picked up along the way.

> Status: early. The registry scaffolding is in place and content is being added.

## Content so far

**The three portal blocks** — Nether Portal, End Portal and End Gateway. They exist in vanilla but
have no item form, so these are placeable items that put down the real vanilla block.

The nether portal is placed turned to face you, the way it works in Bedrock.

**Still water and still lava** — they look like the real fluids but are ordinary blocks, so they stay
exactly where you put them instead of spreading.

**Redstone dust at every power level**, 0 through 15.

**Illegal enchanting.** At the anvil: any enchantment goes onto any item, mutually exclusive
enchantments stack, items that normally can't be enchanted now can be, and the per-enchantment level
limit is gone — every combine adds another level. `/enchant` is lifted the same way, so
`/enchant @s sharpness 255` works.

The vanilla **255** ceiling is gone too. That one lives in the component that stores enchantments on
a stack, and it is enforced in three places, all lifted: `Builder.set` clamped with
`Math.min(level, 255)`, the save codec was `Codec.intRange(1, 255)`, and the constructor threw
outside 0–255. The network path needed nothing — the level was already sent as an unbounded VarInt.

Past 255 the vanilla damage and protection formulas start overflowing into nonsense, which is the
point.

Levels above 10 have no vanilla lang key and would otherwise display as
`enchantment.level.255`, so the mod renders them as Roman numerals — **Sharpness CCLV** — falling
back to plain digits past 3999, where Roman notation stops being readable.

Everything the mod adds appears in vanilla's **Operator Utilities** creative tab — the one with the
command block and the barrier — rather than in a tab of its own, matching where Bedrock keeps this
content. That tab is hidden unless you enable **Operator Items Tab** in Options → Controls and have
operator permission, so turn it on if you can't find the items.

## Requirements

| | |
|---|---|
| Minecraft | 1.21.5 |
| Loader | Fabric Loader 0.16.14+ |
| API | Fabric API 0.128.2+1.21.5 |
| Java | 21 |

## Building

```bash
./gradlew build
```

The remapped jar lands in `build/libs/illegal-items-<version>.jar`.

## Running in dev

```bash
./gradlew runClient
```

`./gradlew runServer` for the dedicated server, `./gradlew runDatagen` to regenerate assets and data.

## Layout

A single `main` source set — no client/common split.

```
src/main/java/net/frostytrix/illegalitems/
├── IllegalItems.java                mod initializer, MOD_ID and the id() helper
├── IllegalItemsClient.java          client entrypoint: renderers, render layers, screens
├── IllegalItemsDataGenerator.java   datagen entrypoint
├── registry/
│   ├── ModBlocks.java               block + block item registration
│   ├── ModItems.java                item registration
│   ├── ModEntities.java             entity type registration
│   ├── ModItemGroups.java           injects everything into the operator tab
│   └── ModEnchantments.java         lets any enchantment go on any item
├── item/
│   └── PortalBlockItem.java         places the nether portal facing the player
├── block/ entity/                   custom classes go here
├── util/
│   └── Numerals.java                Roman numerals for illegal enchantment levels
└── mixin/
    └── client/                      client mixins (illegal_items.client.mixins.json)
```

Because client and common code share one source set, the compiler can no longer stop you from
calling client-only classes (`MinecraftClient`, renderers, `Screen`) from common code. Doing so
crashes a dedicated server, so keep that code in `IllegalItemsClient` and its own packages.

Adding content is a one-liner per entry, e.g.:

```java
public static final Item INFO_UPDATE = ModItems.register("info_update", new Item.Settings());
public static final Block ALLOW = ModBlocks.register("allow", AbstractBlock.Settings.create().strength(-1.0F, 3600000.0F));
```

## License

MIT — see [LICENSE](LICENSE).
