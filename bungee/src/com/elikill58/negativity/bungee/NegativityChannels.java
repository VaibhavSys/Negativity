package com.elikill58.negativity.bungee;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.bungee.BungeeListeners.Report;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.permissions.Perm;
import com.elikill58.negativity.universal.pluginMessages.AlertMessage;
import com.elikill58.negativity.universal.pluginMessages.NegativityMessage;
import com.elikill58.negativity.universal.pluginMessages.ReportMessage;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class NegativityChannels {

	public static boolean isGlobalMessage(NegativityMessage msg) {
		return msg instanceof AlertMessage || msg instanceof ReportMessage;
	}

	public static void manageGlobalChannelMessage(String server, String proxyId, NegativityMessage message) {
		if (message instanceof AlertMessage) {
			AlertMessage alert = (AlertMessage) message;
			String playername = BungeeNegativity.getNameFromUUID(alert.getPlayerUUID());
			Object[] place = new Object[] { "%name%", playername, "%cheat%", alert.getCheat(), "%reliability%",
					alert.getReliability(), "%ping%", alert.getPing(), "%nb%", alert.getAlertsCount(), "%server_name%",
					BungeeNegativity.getServerNameForPlayer(alert.getPlayerUUID()) };
			String alertMessageKey = alert.isMultiple() ? "alert_multiple" : "alert";
			for (ProxiedPlayer pp : ProxyServer.getInstance().getPlayers()) {
				NegativityPlayer nPlayer = NegativityPlayer.getCached(pp.getUniqueId());
				if (Perm.hasPerm(nPlayer, Perm.SHOW_ALERT) && nPlayer.getAccount().isShowAlert()) {
					TextComponent alertMessage = new TextComponent(
							Messages.getMessage(pp.getUniqueId(), alertMessageKey, place));

					ComponentBuilder hoverComponent = new ComponentBuilder(
							Messages.getMessage(pp.getUniqueId(), "alert_hover", place));
					Cheat.CheatHover hoverInfo = alert.getHoverInfo();
					if (hoverInfo != null) {
						hoverComponent.append("\n\n" + Messages.getMessage(hoverInfo.compile(nPlayer)),
								ComponentBuilder.FormatRetention.NONE);
					}
					hoverComponent.append(
							"\n\n" + Messages.getMessage(pp.getUniqueId(), "alert_tp_info", "%playername%", playername),
							ComponentBuilder.FormatRetention.NONE);
					alertMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverComponent.create()));
					alertMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
							"/negativitytp " + playername + " " + server));
					pp.sendMessage(alertMessage);
				}
			}
		} else if (message instanceof ReportMessage) {
			ReportMessage report = (ReportMessage) message;
			Object[] place = new Object[] { "%name%", report.getReported(), "%reason%", report.getReason(), "%report%",
					report.getReporter() };
			boolean hasPermitted = false;
			for (ProxiedPlayer pp : ProxyServer.getInstance().getPlayers()) {
				if (!Perm.hasPerm(NegativityPlayer.getCached(pp.getUniqueId()), Perm.SHOW_REPORT))
					continue;
				hasPermitted = true;
				TextComponent msg = new TextComponent(Messages.getMessage(pp.getUniqueId(), "report", place));
				msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
						new TextComponent[] { new TextComponent(Messages.getMessage(pp.getUniqueId(), "report_hover",
								"%playername%", report.getReported())) }));
				msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
						"/negativitytp " + report.getReported() + " " + server));
				pp.sendMessage(msg);
			}
			if (!hasPermitted) {
				BungeeListeners.report.add(new Report("/server " + server, place));
			}
		}
	}
}
