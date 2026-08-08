package net.frostytrix.illegalitems.client;

/**
 * Carries "this dog's coat is dyed" from the wolf entity onto its render state.
 *
 * <p>The renderer only ever sees the render state, not the entity, so the flag has to be copied
 * across in {@code updateRenderState} where both are in hand. Mixed into
 * {@code WolfEntityRenderState}.
 */
public interface DyedBodyState {
	boolean illegalItems$isDyedBody();

	void illegalItems$setDyedBody(boolean dyedBody);
}
