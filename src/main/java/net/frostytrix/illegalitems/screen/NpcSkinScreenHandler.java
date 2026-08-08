package net.frostytrix.illegalitems.screen;

import net.frostytrix.illegalitems.entity.NpcEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * A skin picker built out of a chest screen: one slot per skin, click to wear it.
 *
 * <p>Reusing {@code GENERIC_9X4} means there is no custom screen to write and nothing to register
 * on the client — the client opens the ordinary four-row chest and sends its clicks to the server,
 * where this handler intercepts them. Only the server ever has this class.
 *
 * <p>The slots are display only: nothing can be taken out or put in, and clicking one sets the skin
 * and closes the screen rather than picking anything up.
 */
public class NpcSkinScreenHandler extends ScreenHandler {
	private static final int COLUMNS = 9;
	private static final int ROWS = 4;
	private static final double REACH = 8.0;

	private final NpcEntity npc;
	private final Inventory icons;

	public NpcSkinScreenHandler(int syncId, PlayerInventory playerInventory, NpcEntity npc) {
		super(ScreenHandlerType.GENERIC_9X4, syncId);
		this.npc = npc;
		this.icons = buildIcons(npc.getSkin());

		for (int row = 0; row < ROWS; row++) {
			for (int column = 0; column < COLUMNS; column++) {
				addSlot(new LockedSlot(icons, column + row * COLUMNS, 8 + column * 18, 18 + row * 18));
			}
		}

		// The player's own inventory, positioned for a four-row container.
		for (int row = 0; row < 3; row++) {
			for (int column = 0; column < COLUMNS; column++) {
				addSlot(new Slot(playerInventory, column + row * 9 + 9,
						8 + column * 18, 103 + row * 18 + 18));
			}
		}

		for (int column = 0; column < COLUMNS; column++) {
			addSlot(new Slot(playerInventory, column, 8 + column * 18, 179));
		}
	}

	private static Inventory buildIcons(int current) {
		SimpleInventory inventory = new SimpleInventory(COLUMNS * ROWS);

		for (int i = 0; i < NpcEntity.SKINS.length; i++) {
			String skin = NpcEntity.SKINS[i];
			ItemStack icon = new ItemStack(iconFor(skin));
			icon.set(DataComponentTypes.ITEM_NAME, Text.literal(skin)
					.formatted(i == current ? Formatting.GREEN : Formatting.WHITE));

			// The one currently worn glints, so it is obvious which is selected.
			if (i == current) {
				icon.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
			}

			inventory.setStack(i, icon);
		}

		return inventory;
	}

	/** A different icon per profession, so the grid is scannable rather than 32 identical papers. */
	private static Item iconFor(String skin) {
		if (skin.startsWith("npc_scientist")) {
			return Items.BREWING_STAND;
		}

		if (skin.startsWith("npc_teacher")) {
			return Items.BOOK;
		}

		if (skin.startsWith("npc_construction")) {
			return Items.BRICKS;
		}

		if (skin.startsWith("npc_apiary")) {
			return Items.HONEYCOMB;
		}

		return Items.EMERALD;
	}

	@Override
	public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
		if (slotIndex >= 0 && slotIndex < NpcEntity.SKINS.length) {
			npc.setSkin(slotIndex);

			// closeHandledScreen is only public on the server-side player, and this only ever runs there.
			if (player instanceof ServerPlayerEntity serverPlayer) {
				serverPlayer.closeHandledScreen();
			}

			return;
		}

		// Everything else, including the player's own inventory, is inert while this is open.
	}

	@Override
	public ItemStack quickMove(PlayerEntity player, int slot) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return npc.isAlive() && player.distanceTo(npc) < REACH;
	}

	/** Display only: cannot be filled, cannot be emptied. */
	private static class LockedSlot extends Slot {
		LockedSlot(Inventory inventory, int index, int x, int y) {
			super(inventory, index, x, y);
		}

		@Override
		public boolean canInsert(ItemStack stack) {
			return false;
		}

		@Override
		public boolean canTakeItems(PlayerEntity player) {
			return false;
		}
	}
}
