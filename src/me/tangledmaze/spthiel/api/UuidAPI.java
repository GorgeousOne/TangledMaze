package me.tangledmaze.spthiel.api;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class UuidAPI {
	
	@Deprecated
	private static String getUUID_alt(String playername) {

		String response = "";

		try {
			response = HTTP.get("https://api.mojang.com/users/profiles/minecraft/" + playername);
			if (response.length() == 0) {
				throw new Exception();
			}

		}catch (Exception ex) {

			try {
				response = HTTP.get("https://api.mojang.com/users/profiles/minecraft/" + playername + "?at="
						+ (long) (System.currentTimeMillis() / 1000));

				if (response.length() == 0)
					throw new Exception();

			} catch (Exception exc) {

				try {
					response = HTTP.get("https://api.mojang.com/users/profiles/minecraft/" + playername + "?at=0");

					if (response.length() == 0)
						throw new Exception();

				} catch (Exception exce) {
					return null;
				}
			}
		}

		String[] resp = response.split("");

		int i = 5;

		while (!resp[i - 5].equalsIgnoreCase("{") || !resp[i - 4].equalsIgnoreCase("\"") ||
			   !resp[i - 3].equalsIgnoreCase("i") || !resp[i - 2].equalsIgnoreCase("d")  ||
			   !resp[i - 1].equalsIgnoreCase("\"")) {
			i++;
		}

		while (!resp[i].equalsIgnoreCase("\""))
			i++;

		int start = 0;
		String result = "";

		while (!resp[i + 1].equalsIgnoreCase("\"")) {

			result += resp[i + 1];
			if (start == 7 || start == 11 || start == 15 || start == 19) {
				result += "-";
			}
			
			i++;
			start++;
		}
		return result;
	}

	@Deprecated
	private static String username_alt(String uuid) {
		
		uuid = uuid.replace("-", "");
		String response = "";
		
		try {
			response = HTTP.get("https://api.mojang.com/user/profiles/" + uuid + "/names");

		} catch (Exception e) {
			return null;
		}

		System.out.println(response);
		String[] resp = response.split("");

		int i = resp.length;

		while (!resp[i - 6].equalsIgnoreCase("\"") || !resp[i - 5].equalsIgnoreCase("n")
				|| !resp[i - 4].equalsIgnoreCase("a") || !resp[i - 3].equalsIgnoreCase("m")
				|| !resp[i - 2].equalsIgnoreCase("e") || !resp[i - 1].equalsIgnoreCase("\""))
			i--;

		while (!resp[i].equalsIgnoreCase("\""))
			i++;

		String result = "";

		while (!resp[i + 1].equalsIgnoreCase("\"")) {
			result += resp[i + 1];
			i++;
		}

		return result;
	}

	public static String uubp(String playername) {
		return getUUID(playername);
	}
	
	public static String getUUID(String playername) {
		
		File f = new File("plugins/MDApi/names", playername.toLowerCase() + ".yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(f);
		
		if(config.isSet("uuid"))
			return config.getString("uuid");
			
		else {
			String uuid = getUUID_alt(playername);
			savePlayer(uuid,playername);
			return playername;
		}
	}

	public static String username(String uuid) {
		
		File f = new File("plugins/MDApi/uuids", uuid + ".yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(f);
		
		if(config.isSet("last"))
			return config.getString("last");
		
		else {
			String playername = username_alt(uuid);
			savePlayer(uuid,playername);
			return playername;
		}
	}
	
	public static void savePlayer(String uuid, String playername){

		File f = new File("plugins/MDApi/uuids", uuid + ".yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(f);
		
		if (f.exists()) {
			
			if (config.isSet("last")) {
				if (!config.getString("last").equalsIgnoreCase(playername)) {
					config.set("last", playername);
				}
			}else
				config.set("last", playername);

			if (config.isSet("all")) {
				if (config.get("all") instanceof Collection<?>) {
					List<String> list = config.getStringList("all");
					if (!list.contains(playername)) {
						list.add(playername);
						config.set("all", list);
					}
				} else {
					List<String> list = new LinkedList<String>();
					list.add(playername);
					config.set("all", list);
				}
			}
			
		} else {
			config.set("last", playername);

			List<String> list = new LinkedList<String>();
			list.add(playername);
			config.set("all", list);
			
		}
		try {
			config.save(f);
		} catch (IOException e) {}

		File file = new File("plugins/MDApi/names", playername.toLowerCase() + ".yml");
		FileConfiguration c = YamlConfiguration.loadConfiguration(file);
		
		if(file.exists()){
			if(!c.isSet("uuid")){
				c.set("uuid", uuid + "");
			} else {
				if(!c.getString("uuid").equalsIgnoreCase(uuid + "")){
					c.set("uuid", uuid + "");
				}
			}
		} else {
			
			c.set("uuid", uuid + "");
			
		}
		
		try {
			c.save(file);
		} catch (IOException e) {}
		
	}

	protected static void savePlayer(Player p) {
		
		File f = new File("plugins/MDApi/uuids", p.getUniqueId() + ".yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(f);
		
		if (f.exists()) {
			
			if (config.isSet("last")) {
				if (!config.getString("last").equalsIgnoreCase(p.getName())) {
					config.set("last", p.getName());
				}
			}else
				config.set("last", p.getName());

			if (config.isSet("all")) {
				if (config.get("all") instanceof Collection<?>) {
					List<String> list = config.getStringList("all");
					if (!list.contains(p.getName())) {
						list.add(p.getName());
						config.set("all", list);
					}
				} else {
					List<String> list = new LinkedList<String>();
					list.add(p.getName());
					config.set("all", list);
				}
			}
			
		} else {
			config.set("last", p.getName());

			List<String> list = new LinkedList<String>();
			list.add(p.getName());
			config.set("all", list);
			
		}
		try {
			config.save(f);
		} catch (IOException e) {}

		File file = new File("plugins/MDApi/names", p.getName().toLowerCase() + ".yml");
		FileConfiguration c = YamlConfiguration.loadConfiguration(file);
		
		if(file.exists()){
			if(!c.isSet("uuid")){
				c.set("uuid", p.getUniqueId() + "");
			} else {
				if(!c.getString("uuid").equalsIgnoreCase(p.getUniqueId() + "")){
					c.set("uuid", p.getUniqueId() + "");
				}
			}

			if (config.isSet("all")) {
				if (config.get("all") instanceof Collection<?>) {
					List<String> list = config.getStringList("all");
					if (!list.contains(p.getUniqueId() + "")) {
						list.add(p.getUniqueId() + "");
						config.set("all", list);
					}
				} else {
					List<String> list = new LinkedList<String>();
					list.add(p.getName());
					config.set("all", list);
				}
			}
		} else
			c.set("uuid", p.getUniqueId() + "");
		
		try {
			c.save(file);
		} catch (IOException e) {}
	}
}