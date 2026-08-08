package net.frostytrix.illegalitems.registry;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.frostytrix.illegalitems.IllegalItems;
import net.frostytrix.illegalitems.entity.AgentEntity;
import net.frostytrix.illegalitems.entity.HumanEntity;
import net.frostytrix.illegalitems.entity.NpcEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

/** Entity type registry for the bedrock-only entities (agent, npc, balloon, ...). */
public final class ModEntities {
	private ModEntities() {
	}

	/** Dock's Indev test human — see {@link HumanEntity}. */
	public static final EntityType<HumanEntity> HUMAN = register("human",
			EntityType.Builder.create(HumanEntity::new, SpawnGroup.CREATURE)
					.dimensions(0.6F, 1.8F)
					.eyeHeight(1.62F)
					.maxTrackingRange(10));

	/** The Education Edition agent — see {@link AgentEntity}. */
	public static final EntityType<AgentEntity> AGENT = register("agent",
			EntityType.Builder.create(AgentEntity::new, SpawnGroup.MISC)
					.dimensions(0.6F, 0.93F)
					.eyeHeight(0.7F)
					.maxTrackingRange(10));

	/** The Education Edition NPC — see {@link NpcEntity}. */
	public static final EntityType<NpcEntity> NPC = register("npc",
			EntityType.Builder.create(NpcEntity::new, SpawnGroup.MISC)
					.dimensions(0.6F, 2.1F)
					.eyeHeight(1.9F)
					.maxTrackingRange(10));

	public static <T extends Entity> EntityType<T> register(String name, EntityType.Builder<T> builder) {
		RegistryKey<EntityType<?>> key = RegistryKey.of(RegistryKeys.ENTITY_TYPE, IllegalItems.id(name));
		return Registry.register(Registries.ENTITY_TYPE, key, builder.build(key));
	}

	public static void initialize() {
		FabricDefaultAttributeRegistry.register(HUMAN, HumanEntity.createHumanAttributes());
		FabricDefaultAttributeRegistry.register(AGENT, AgentEntity.createAgentAttributes());
		FabricDefaultAttributeRegistry.register(NPC, NpcEntity.createNpcAttributes());
	}
}
