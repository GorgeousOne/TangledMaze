package me.tangledmaze.gorgeousone.rawmessage;

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
		this.color = Color.DEFAULT;
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
	    
	    out.append(color);

	    if(bold)
	      out.append(Color.BOLD);
	    if(italic)
	      out.append(Color.ITALIC);
	    if(obfuscated)
	      out.append(Color.OBFUSCATED);
	    if(underlined)
	      out.append(Color.UNDERLINED);
	    if(strikethrough)
	      out.append(Color.STRIKETHROUGH);

	    return out.toString();
	  }
}