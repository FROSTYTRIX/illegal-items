package net.frostytrix.illegalitems.registry;

import net.frostytrix.illegalitems.IllegalItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

/** Entity type registry for the bedrock-only entities (agent, npc, balloon, ...). */
public final class ModEntities {
	private ModEntities() {
	}

	public static <T extends Entity> EntityType<T> register(String name, EntityType.Builder<T> builder) {
		RegistryKey<EntityType<?>> key = RegistryKey.of(RegistryKeys.ENTITY_TYPE, IllegalItems.id(name));
		return Registry.register(Registries.ENTITY_TYPE, key, builder.build(key));
	}

	public static void initialize() {
	}
}
