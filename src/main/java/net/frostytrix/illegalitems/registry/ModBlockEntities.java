package net.frostytrix.illegalitems.registry;

import net.frostytrix.illegalitems.IllegalItems;
import net.frostytrix.illegalitems.block.entity.MixedSlabBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

/** Block entity types. Only the mixed slab needs one so far. */
public final class ModBlockEntities {
	private ModBlockEntities() {
	}

	public static final BlockEntityType<MixedSlabBlockEntity> MIXED_SLAB = Registry.register(
			Registries.BLOCK_ENTITY_TYPE, IllegalItems.id("mixed_slab"),
			FabricBlockEntityTypeBuilder.create(MixedSlabBlockEntity::new, ModBlocks.MIXED_SLAB).build());

	public static void initialize() {
	}
}
