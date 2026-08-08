package net.frostytrix.illegalitems.registry;

import com.mojang.serialization.Codec;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.frostytrix.illegalitems.IllegalItems;
import net.minecraft.network.codec.PacketCodecs;

/** Per-entity state the mod keeps around. */
public final class ModAttachments {
	private ModAttachments() {
	}

	/**
	 * Set on a tamed wolf whose coat has taken the dye instead of its collar.
	 *
	 * <p>Persistent so it survives a reload, and synced because the client needs it to know whether
	 * to tint the wolf and hide its collar.
	 */
	public static final AttachmentType<Boolean> DYED_BODY = AttachmentRegistry.<Boolean>builder()
			.persistent(Codec.BOOL)
			.syncWith(PacketCodecs.BOOLEAN, AttachmentSyncPredicate.all())
			.buildAndRegister(IllegalItems.id("dyed_body"));

	public static void initialize() {
	}
}
