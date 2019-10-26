package me.gorgeousone.tangledmaze.rawmessage;

import java.util.ArrayList;

public class HoverText {

	private RawElement parent;
	private ArrayList<ColorText> texts;

	public HoverText(String text, RawElement parent) {
		this.parent = parent;
		
		texts = new ArrayList<>();
		texts.add(new ColorText(text));
	}
	
	public HoverText color(Color color) {
		texts.get(texts.size()-1).color(color);
		return this;
	}
	
	public HoverText bold(boolean bold) {
		texts.get(texts.size()-1).bold(bold);
		return this;
	}

	public HoverText italic(boolean italic) {
		texts.get(texts.size()-1).italic(italic);
		return this;
	}

	public HoverText obfuscated(boolean obfuscated) {
		texts.get(texts.size()-1).obfuscated(obfuscated);
		return this;
	}

	public HoverText underlined(boolean underlined) {
		texts.get(texts.size()-1).underlined(underlined);
		return this;
	}

	public HoverText strikethrough(boolean strikethrough) {
		texts.get(texts.size()-1).strikethrough(strikethrough);
		return this;
	}
	
	public HoverText append(String text) {
		texts.add(new ColorText(text));
		return this;
	}
	
	public RawElement flipHover() {
		return parent;
	}
	
	@Override
	public String toString() {
		StringBuilder hoverText = new StringBuilder(",\"hoverEvent\":{\"action\":\"show_text\",\"value\":[");
		
		for(ColorText element : texts)
			hoverText.append("{").append(element.toString()).append("},");

		hoverText.deleteCharAt(hoverText.length()-1);
		hoverText.append("]}");
		
		return hoverText.toString();
	}
}