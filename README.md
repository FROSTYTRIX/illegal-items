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

**The bubble column**, which has no item form in vanilla. It places the real
`minecraft:bubble_column` — not a copy — and behaves like the Bedrock one: dry, standing on nothing,
and gone again on the next tick without leaving a block of water behind.

Vanilla hands back still water from `getFluidState` outright, with no property behind it and no
position to vary on, so the mod adds the property itself. It is named for the wet case on purpose:
a boolean property lists its values as `[true, false]` and a block's default state takes the first
of each, so `wet=true` becomes the default at no cost and every column the game makes for itself
carries on untouched.

**Still water and still lava** — real fluids that never spread. You swim and drown in the water,
burn in the lava, and it lights and sets fire to its surroundings exactly like the vanilla stuff;
it just stays where you put it. They sit in the `minecraft:water` and `minecraft:lava` fluid tags so
other mods treat them as water and lava, and filling a bucket from one gives an ordinary water or
lava bucket.

**Redstone dust at every power level**, 0 through 15.

**Fire and soul fire** as placeable items. Placement follows vanilla's own rules, so fire still needs
something to burn on and soul fire still needs soul sand or soul soil under it.

**A tipped arrow with no potion at all.** Vanilla always gives tipped arrows potion contents, so this
one looks tipped but flies and hits like a plain arrow. It is in the `minecraft:arrows` tag, so bows
and crossbows fire it normally.

It sits alongside vanilla's **Uncraftable Tipped Arrow**, which the operator tab also carries. The
two are not the same: the uncraftable one is a real `minecraft:tipped_arrow` holding contents with no
potion in them, while this is its own item with no contents whatsoever. They look alike and fly
alike, and the mod keeps both because they are two different ways of being nothing.

**Dyable dogs.** Dye can end up on the animal itself instead of its collar, in which case the collar
is hidden:

- re-applying the collar's **current** colour always dyes the coat
- a **new** colour dyes it 5% of the time
- a **new** colour on an already-dyed dog strips the coat back to an ordinary collared wolf

Wild wolves are unaffected, and a dyed coat is saved with the wolf.

**Anything consumable with Infinity never runs out.** Put Infinity on a steak, a potion or a bucket
of milk at the anvil and you can use it forever — you still get the hunger, the effects, the sound
and the particles, the stack just never shrinks. Endless potions do not leave empty bottles behind
either, since vanilla only hands one back when the stack actually got smaller.

**An invisible map.** It carries the blank map texture in the inventory, but disappears the moment
it is in a hand — while still being held up in both hands, which stay perfectly visible.

**A spawn egg that spawns nothing**, called `item.spawn_egg.name`. Right-clicking a block swings
your arm and achieves precisely nothing.

**A hay respawn anchor**, from a PhoenixSC thumbnail. It charges from glowstone and glows brighter
with each one, but it will not set your spawn and it will not explode.

**The Agent** from Education Edition. Faithful to how it behaves there, which is mostly by not
doing things: it never moves on its own, nothing can hurt it, and it passes straight through blocks
and entities. Right-click it to open its 27 slot inventory, which is saved with it. Spawn it with
the Agent spawn egg.

Nothing can damage it, but `/kill` still removes it — otherwise there would be no way to get rid of
one. It hovers gently on the spot, taken from its Bedrock animation.

**The NPC** from Education Edition. No AI at all: it never moves, ignores gravity, cannot be damaged
or affected by potions, and will not be pushed around. Commands still remove it.

It stands with its arms folded like a villager and turns its head to watch you.

**Sneak and right-click to open the skin picker**: a grid of all 32, one slot each, click to wear it.
The current one is green and glints, and the icons vary by profession so the grid is scannable.
Plain right-click is left free for the dialogue interface. The choice is saved with the NPC.

**Steve**, Dock's human test mob from Indev 0.31, which lasted about two days.

**The full building palette in netherite** — stairs, slab, wall, fence, fence gate, door, trapdoor,
pressure plate and button. Vanilla stops at the plain block, so these fill it in. They keep the
netherite block's hardness, blast resistance and sounds, and all of them need a diamond pickaxe or
better to drop.

The door and trapdoor ignore right-clicks and answer only to redstone, like their iron counterparts.
The gate opens with the iron door's clunk rather than a wooden creak.

**The Uncraftable family** — potion, splash potion, lingering potion and tipped arrow — added to the
operator tab. These are not new items: each is an ordinary vanilla item carrying no potion contents,
and the four of them are the whole set, being exactly the items with an `.effect.empty` translation
key. Brewing always attaches contents and no creative tab lists them, so `/give` was the only way to
hold one. Drinking or throwing one does nothing, the arrow flies and hits like a plain one, and the
magenta colouring is vanilla's fallback for a potion with no effects to tint it.

**A missing texture block.** Its model points at a texture that deliberately does not exist, so what
you get is the game's real black-and-magenta placeholder rather than a picture of one. Costs one
expected "Missing resource" warning per resource reload.

**Cave Game grass and cobblestone**, from back when Minecraft was still called that.

**The border block** from Education Edition. It looks like a wall, but it seals its entire column —
floor to sky — so nothing can climb, tunnel, swim or walk over or under it. Non-operators also
cannot build or dig anywhere in a sealed column, which includes the border itself. It is
unbreakable, immune to pistons, and trails red dust.

It connects to other borders and to vanilla walls, being in the `#minecraft:walls` tag.

Only operators **in creative** are waved through — being an operator counts for nothing once you
drop into survival, where the border treats you like anybody else. The ender dragon ignores it, and
projectiles and explosions treat it as an ordinary wall, so an ender pearl still gets you across.

**The Nether Reactor** from Pocket Edition, working. Build the shell around the core and use it:

```
bottom          middle          top
G C G           C . C           . C .
C C C           . R .           C C C
G C G           C . C           . C .
```

`G` gold block, `C` cobblestone, `R` the core, `.` air — 4 gold and 14 cobblestone. Note the air:
the middle layer's four edges and the top layer's four corners must be empty.

The netherrack spire goes up instantly, then over the next 45 seconds the shell creeps from glowing
obsidian to plain obsidian while glowstone, quartz and odds and ends pour out and angry zombified
piglins climb after them. When the last block turns, the core is spent.

Breaking a core gives back 3 diamonds and 6 iron ingots.

Pocket Edition also refused to work outside Y 4–96, a limit that came from its 128 block tall
worlds. That is dropped here, since it would rule out most of a 1.21.5 world.

**The update block** (`info_update`) — Bedrock's "update!" block. An ordinary solid block that
breaks by hand and drops itself; the texture is the whole point.

**Broken leather armour** — a full set with vanilla leather's stats. The items themselves are
invisible in the inventory and in hand, and worn they render as the game's own black-and-magenta
missing texture, because the equipment asset points at a texture that deliberately does not exist.
The two "Missing resource" warnings this logs on each resource reload are expected.

**Every mob rides minecarts**, the ender dragon and the wither included. Players still board by
right-clicking as usual, and minecarts still do not ride each other.

**Giant babies.** One baby mob in twenty is born the size of an adult while keeping its baby
proportions — hitbox and all. Whether a mob is one of them comes from its UUID, so it never changes
and needs nothing saved or synced.

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

## Getting hold of it all

Everything the mod adds is obtainable in survival. Nothing needs creative or commands.

**Straight ports of the original recipe**

| | |
|---|---|
| Nether Reactor Core | 6 iron and 3 diamonds, as on Pocket Edition — the same 6 and 3 you get back for breaking one |
| Hay Respawn Anchor | the respawn anchor recipe with hay bales in place of the crying obsidian |

**Crafted**

| | |
|---|---|
| Nether Portal | obsidian around a flint and steel |
| End Portal | end stone around an eye of ender |
| End Gateway | obsidian around a bottle of dragon's breath |

The portals come two other ways as well. They turn up in the chests of the dimension they belong to
— the nether one in bastions (25% in the treasure room, 10% elsewhere), and both end ones in end
city treasure (20% and 15%).

Or you can **mine them straight out of the world**, with a netherite pickaxe carrying **Efficiency
10 and Unbreaking 10**. Both are past vanilla's ceilings of 5 and 3, so the only way to hold that
pickaxe is the mod's own illegal enchanting: a tool that should not exist is what it takes to take a
block that should not move.

Nothing else will do it — a lesser pickaxe is not slower, it simply never gets anywhere, exactly as
an unbreakable block should behave. At the minimum Efficiency 10 a portal takes three seconds, and
every level past that pulls it down: about 1.4 seconds at 15, under a second at 20. Unbreaking is
part of the price of entry and does nothing for the digging. Only the real block drops the item, so
a portal still has to be found or lit first.
| Still water / lava | the bucket plus a honey block, since honey is what stops it running |
| Fire | a fire charge and blaze powder — Soul Fire is that fire plus soul sand |
| Cave Game grass / cobblestone | the modern block plus a cobweb, for the dust of ages |
| Update Block | dirt, a clock and lime dye, one of each |
| Border | obsidian around a cobblestone wall |
| Missing Texture | black and magenta dye chequered across the grid, the two colours it shows |
| Broken leather armour | an honest leather piece plus a missing texture block, which ruins it |
| Invisible Map | a map and a glass pane |
| No-effect Tipped Arrow | eight arrows around an empty glass bottle |
| Bubble Column | soul sand and a water bucket, which is what makes one in the world |

**Stonecutter**

The whole netherite palette comes off a netherite block — stairs, wall, fence, gate, door, trapdoor,
pressure plate and button one apiece, slabs two.

Redstone dust at every power level is cut from a **redstone block**, nine at a time: one block in,
nine pieces of dust already charged to whichever level you picked.

**The Uncraftable family, made craftable, which is the joke**

A bottle and **two** fermented spider eyes — fermenting twice cancels out, so what you are left with
carries nothing at all. Add gunpowder for the splash version and dragon's breath on top of that for
the lingering one, exactly as vanilla escalates a real potion. The tipped arrow is eight arrows
around a fermented spider eye; vanilla already crafts arrows around a lingering potion, so this
deliberately does not collide with it.

They are built from raw ingredients rather than from a potion, so no real potion can be fed in and
destroyed by accident.

**Spawn eggs, found in structure chests**

| | | |
|---|---|---|
| Agent | ancient city | 20% |
| NPC | village houses | 10% |
| Steve | abandoned mineshaft | 15% |
| `item.spawn_egg.name` | buried treasure | 50% |

Each is hidden where it belongs: the Agent among the other machinery nobody can explain, the NPCs
where they would be standing anyway, Steve forgotten for about as long as he lasted in Indev, and
the egg that spawns nothing under an X on a treasure map — so half the time, digging up the loot
gets you a lifetime supply of no mob at all.

Only the game's own loot tables are touched; a data pack that has replaced one is left alone.

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
│   ├── PortalBlockItem.java         places the nether portal facing the player
│   └── RedstoneWireItem.java        places wire at a fixed power level
├── fluid/
│   ├── StillWaterFluid.java         water with the spreading removed
│   └── StillLavaFluid.java          lava with the spreading removed
├── block/
│   └── StillFluidBlock.java         block form of the two still fluids
├── entity/                          custom classes go here
├── util/
│   ├── Numerals.java                Roman numerals for illegal enchantment levels
│   └── GiantBabies.java             picks which babies spawn at adult size
└── mixin/
    └── client/                      client mixins (illegal_items.client.mixins.json),
                                     including the dyable-dog rendering
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
