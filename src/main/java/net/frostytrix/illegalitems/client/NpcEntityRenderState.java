package net.frostytrix.illegalitems.client;

import net.minecraft.client.render.entity.state.LivingEntityRenderState;

/** Carries the chosen skin index across to the renderer, which never sees the entity itself. */
public class NpcEntityRenderState extends LivingEntityRenderState {
	public int skin;
}
