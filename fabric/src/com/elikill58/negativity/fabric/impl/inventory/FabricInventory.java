package com.elikill58.negativity.fabric.impl.inventory;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.api.inventory.Inventory;
import com.elikill58.negativity.api.inventory.InventoryType;
import com.elikill58.negativity.api.inventory.PlatformHolder;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.fabric.impl.item.FabricItemStack;

import net.minecraft.item.Item;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class FabricInventory extends Inventory {

	private final ScreenHandler inv;
	
	public FabricInventory(ScreenHandler inv) {
		this.inv = inv;
	}
	
	@Override
	public InventoryType getType() {
		return InventoryType.get(inv.getType().toString());
	}

	@Override
	public ItemStack get(int slot) {
		return new FabricItemStack(inv.getSlot(slot).getStack());
	}

	@Override
	public void set(int slot, ItemStack item) {
		inv.setStackInSlot(slot, inv.getRevision(), (net.minecraft.item.ItemStack) item.getDefault());
	}

	@Override
	public void remove(int slot) {
		inv.setStackInSlot(slot, inv.getRevision(), null);
	}

	@Override
	public void clear() {
		inv.slots.forEach(s -> s.setStack(null));
	}

	@Override
	public void addItem(ItemStack build) {
		for(Slot s : inv.slots) {
			if(s.isEnabled() && !s.hasStack()) {
				s.setStack((net.minecraft.item.ItemStack) build.getDefault());
				return;
			}
		}
	}

	@Override
	public int getSize() {
		return inv.slots.size();
	}

	@Override
	public String getInventoryName() {
		return inv.toString();
	}

	@Override
	public @Nullable PlatformHolder getHolder() {
		return null;
	}

	@Override
	public Object getDefault() {
		return inv;
	}
	
	@Override
	public boolean contains(Material type) {
		Item i = (Item) type.getDefault();
		return inv.slots.stream().filter(s -> s.hasStack() && s.getStack().getItem().equals(i)).count() > 0;
	}
}
