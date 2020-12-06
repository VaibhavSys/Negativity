package com.elikill58.negativity.sponge;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

import org.checkerframework.checker.nullness.qual.Nullable;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.gson.GsonConfigurationLoader;

public class SpongeUpdateChecker {
	
	private static final String BASE_URL = "https://ore.spongepowered.org/api/v2/";
	
	private static String latestVersion;
	
	private static boolean checkForUpdate() throws IOException {
		Optional<String> currentVersion = SpongeNegativity.getInstance().getContainer().getVersion();
		if (!currentVersion.isPresent())
			return false;
		
		String session = openSession();
		try {
			HttpURLConnection connection = prepareConnection("projects/negativity/versions?limit=1", session);
			ConfigurationNode response = readJsonResponse(connection);
			latestVersion = response.getNode("result", 0, "name").getString();
			return !currentVersion.get().equals(latestVersion);
		} finally {
			closeSession(session);
		}
	}
	
	private static String openSession() throws IOException {
		HttpURLConnection connection = prepareConnection("authenticate", null);
		connection.setRequestMethod("POST");
		ConfigurationNode response = readJsonResponse(connection);
		String session = response.getNode("session").getString();
		if (session == null) {
			throw new IOException("Could not open OreApi session: " + toJson(response));
		}
		return session;
	}
	
	private static void closeSession(String session) throws IOException {
		HttpURLConnection connection = prepareConnection("sessions/current", session);
		connection.setRequestMethod("DELETE");
		connection.connect();
		if (connection.getResponseCode() != 204) {
			SpongeNegativity.getInstance().getLogger().error("Could not close Ore API session correctly: {} - {}",
				connection.getResponseCode(), connection.getResponseMessage());
		}
	}
	
	private static HttpURLConnection prepareConnection(String href, @Nullable String session) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) new URL(BASE_URL + href).openConnection();
		connection.setRequestProperty("User-Agent", "Negativity");
		if (session != null) {
			connection.addRequestProperty("Authorization", "OreApi session=\"" + session + "\"");
		}
		return connection;
	}
	
	private static ConfigurationNode readJsonResponse(HttpURLConnection connection) throws IOException {
		return GsonConfigurationLoader.builder()
			.setSource(() -> new BufferedReader(new InputStreamReader(connection.getInputStream())))
			.build().load();
	}
	
	private static String toJson(ConfigurationNode node) throws IOException {
		try (StringWriter writer = new StringWriter()) {
			GsonConfigurationLoader.builder().setSink(() -> new BufferedWriter(writer)).build().save(node);
			return writer.toString();
		}
	}
	
	public static boolean isUpdateAvailable() {
		try {
			return checkForUpdate();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static String getDownloadUrl() {
		return "https://ore.spongepowered.org/Elikill58/Negativity/versions/" + latestVersion;
	}
	
	public static String getVersionString() {
		return latestVersion;
	}
}
