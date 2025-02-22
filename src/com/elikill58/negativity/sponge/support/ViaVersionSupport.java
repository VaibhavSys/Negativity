package com.elikill58.negativity.sponge.support;

import org.spongepowered.api.entity.living.player.Player;

import com.elikill58.negativity.universal.Version;
import com.viaversion.viaversion.api.Via;

public class ViaVersionSupport {

	public static Version getPlayerVersion(Player p) {
		return Version.getVersionByProtocolID(Via.getAPI().getPlayerVersion(p.getUniqueId()));
	}
}
