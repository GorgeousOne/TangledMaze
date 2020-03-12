package me.gorgeousone.tangledmaze.rawmessage;

public class ColorText {
	
	private String text;
	private Color color;
	
	private boolean
			bold,
			italic,
			obfuscated,
			underlined,
			strikethrough;
	
	public ColorText(String text) {
		
		this.text = text;
		
		bold = false;
		italic = false;
		obfuscated = false;
		underlined = false;
		strikethrough = false;
	}
	
	public ColorText color(Color color) {
		this.color = color;
		return this;
	}
	
	public ColorText bold(boolean bold) {
		this.bold = bold;
		return this;
	}
	
	public ColorText italic(boolean italic) {
		this.italic = italic;
		return this;
	}
	
	public ColorText obfuscated(boolean obfuscated) {
		this.obfuscated = obfuscated;
		return this;
	}
	
	public ColorText underlined(boolean underlined) {
		this.underlined = underlined;
		return this;
	}
	
	public ColorText strikethrough(boolean strikethrough) {
		this.strikethrough = strikethrough;
		return this;
	}
	
	public String toString() {
		
		StringBuilder out = new StringBuilder("\"text\":\"" + text + "\"");
		
		if(color != null)
			out.append(",").append(color);
		if (bold)
			out.append(",").append(Color.BOLD);
		if (italic)
			out.append(",").append(Color.ITALIC);
		if (obfuscated)
			out.append(",").append(Color.OBFUSCATED);
		if (underlined)
			out.append(",").append(Color.UNDERLINED);
		if (strikethrough)
			out.append(",").append(Color.STRIKETHROUGH);
		
		return out.toString();
	}
}