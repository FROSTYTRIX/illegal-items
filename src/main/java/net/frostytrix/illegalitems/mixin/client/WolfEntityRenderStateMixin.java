package net.frostytrix.illegalitems.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.frostytrix.illegalitems.client.DyedBodyState;
import net.minecraft.client.render.entity.state.WolfEntityRenderState;

/** Storage for the dyed-coat flag; see {@link DyedBodyState}. */
@Mixin(WolfEntityRenderState.class)
public class WolfEntityRenderStateMixin implements DyedBodyState {

	@Unique
	private boolean illegalItems$dyedBody;

	@Override
	public boolean illegalItems$isDyedBody() {
		return illegalItems$dyedBody;
	}

	@Override
	public void illegalItems$setDyedBody(boolean dyedBody) {
		this.illegalItems$dyedBody = dyedBody;
	}
}
