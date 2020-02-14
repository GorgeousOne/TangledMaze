package me.gorgeousone.tangledmaze.rawmessage;

public enum Color {

	WHITE(",\"color\":\"white\""),
	YELLOW(",\"color\":\"yellow\""),
	LIGHT_PURPLE(",\"color\":\"light_purple\""),
	LIGHT_RED(",\"color\":\"red\""),
	LIGHT_BLUE(",\"color\":\"aqua\""),
	LIGHT_GREEN(",\"color\":\"green\""),
	BLUE(",\"color\":\"blue\""),
	GRAY(",\"color\":\"dark_gray\""),
	LIGHT_GRAY(",\"color\":\"gray\""),
	ORANGE(",\"color\":\"gold\""),
	PURPLE(",\"color\":\"dark_purple\""),
	RED(",\"color\":\"dark_red\""),
	TURQUOISE(",\"color\":\"dark_aqua\""),
	GREEN(",\"color\":\"dark_green\""),
	DARK_BLUE(",\"color\":\"dark_blue\""),
	BLACK(",\"color\":\"black\""),
	OBFUSCATED(",obfuscated:true"),
	BOLD(",bold:true"),
	ITALIC(",italic:true"),
	UNDERLINED(",underlined:true"),
	STRIKETHROUGH(",strikethrough:true"),
	DEFAULT("");

	private String format;

	Color(String format) {
		this.format = format;
	}

	public String toString() {
		return format;
	}
}