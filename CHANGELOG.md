# Illegal Items — 1.1.0

Contraband Goes Legit. Every last item in the mod is now **obtainable in
survival** — 50 recipes, spawn eggs buried in structure chests, and portals you
can *mine out of the world* with a pickaxe that has no business existing. Plus
the **full netherite building palette** vanilla never gave you, and a fix for
Steves quietly vanishing.

## 🧾 Everything is obtainable now

- **50 recipes, no creative required.** Nothing in the mod needs commands or the
  operator tab any more. The Nether Reactor Core uses Pocket Edition's own
  recipe — 6 iron and 3 diamonds, exactly what breaking one gives back — and the
  hay respawn anchor is the vanilla anchor with hay bales in place of the crying
  obsidian.
- **Every recipe is in the recipe book**, so they show up as you gather the
  ingredients rather than only working if you happen to know the shape.
- **Some favourites:** still water and lava are the bucket plus a **honey
  block**, because honey is what stops it running. The Missing Texture block is
  **black and magenta dye chequered across the grid** — made of the two colours
  it shows. The Update Block is dirt, a clock and lime dye. Cave Game blocks are
  the modern block plus a **cobweb**, for the dust of ages.
- **Redstone at every power level** is cut from a **redstone block** on the
  stonecutter, nine at a time — one block in, nine pieces of dust already charged
  to whichever level you picked.

## ⛏️ Mine the portals out of the world

- **A netherite pickaxe with Efficiency 10 and Unbreaking 10 breaks portal
  blocks.** Both are past vanilla's ceilings of 5 and 3, so the only way to hold
  that pickaxe is the mod's own illegal enchanting — a tool that shouldn't exist
  is what it takes to take a block that shouldn't move.
- **Nothing else will do it.** A lesser pickaxe isn't slower, it simply never
  gets anywhere, exactly as an unbreakable block should behave.
- **Three seconds at the minimum**, and every level past the tenth pulls it
  down — about 1.4 seconds at Efficiency 15, under a second at 20.
- **Or find them in chests:** the nether portal in bastions, both end ones in end
  city treasure. Each dimension keeps its own portal where it keeps its valuables.

## 🥚 Spawn eggs, hidden where they belong

- **The Agent** in ancient cities, among the other machinery nobody can explain.
- **The NPCs** in village houses, where they'd be standing anyway.
- **Steve** down an abandoned mineshaft, forgotten for about as long as he lasted
  in Indev.
- **`item.spawn_egg.name`** under an X on a treasure map — so half the time,
  digging up the loot gets you a lifetime supply of no mob at all.

## 🪨 The netherite palette

- **Stairs, slab, wall, fence, fence gate, door, trapdoor, pressure plate and
  button.** Vanilla stops at the plain block; this fills it in. All of it comes
  off a netherite block on the **stonecutter**, and all of it keeps netherite's
  hardness, blast resistance and sounds.
- The door and trapdoor ignore right-clicks and answer only to redstone, like
  their iron counterparts. The gate opens with the iron door's clunk rather than
  a wooden creak.

## 🫧 New contraband

- **The Bubble Column.** Places a real `minecraft:bubble_column` — dry, rising,
  and gone again on the next tick, as on Bedrock. It bursts its bubbles the
  instant it goes down, and carries you at **exactly the speed a water column
  does**, which took some doing: vanilla's numbers are tuned against water's
  near-cancelled gravity and lose outright to open air.
- **The Missing Texture block.** Its model points at a texture that deliberately
  doesn't exist, so what you get is the game's real black-and-magenta
  placeholder rather than a picture of one.
- **The Uncraftable family** — potion, splash potion, lingering potion and tipped
  arrow — added to the operator tab. Not new items: each is an ordinary vanilla
  item carrying no potion contents, and those four are the whole set, being
  exactly the items vanilla gives an `.effect.empty` name to. **Made craftable,
  which is the joke:** a bottle and *two* fermented spider eyes, since fermenting
  twice cancels out and leaves you holding nothing at all.
- They sit alongside the mod's own **no-effect tipped arrow**, which is a
  different thing entirely — the uncraftable one holds contents with no potion in
  them, this one has no contents whatsoever. Two different ways of being nothing.

## 🐛 Bug fixes

- **Steves stop vanishing.** Spawn a crowd and they'd quietly disappear one by
  one. Steve was an ordinary despawnable creature: anything more than 32 blocks
  from a player rolled to vanish every tick, and past 128 went instantly. Because
  he hops and wanders constantly a crowd disperses fast, and the roll is *per
  mob*, so fifty of them evaporated while a single one looked fine. He now stays
  put, the way vanilla animals do.
