package me.gorgeousone.tangledmaze.rawmessage;

import java.util.ArrayList;
import java.util.List;

public class RawElement {
	
	private RawMessage parent;
	private List<ColorText> texts;
	private HoverText hoverText;
	private ClickText clickText;
	
	public RawElement(String text, RawMessage parent) {
		this.parent = parent;
		texts = new ArrayList<>();
		texts.add(new ColorText(text));
	}
	
	public RawElement append(String text) {
		texts.add(new ColorText(text));
		return this;
	}
	
	public RawElement color(Color color) {
		texts.get(texts.size()-1).color(color);
		return this;
	}
	
	public RawElement bold(boolean bold) {
		texts.get(texts.size()-1).bold(bold);
		return this;
	}

	public RawElement italic(boolean italic) {
		texts.get(texts.size()-1).italic(italic);
		return this;
	}

	public RawElement obfuscated(boolean obfuscated) {
		texts.get(texts.size()-1).obfuscated(obfuscated);
		return this;
	}

	public RawElement underlined(boolean underlined) {
		texts.get(texts.size()-1).underlined(underlined);
		return this;
	}

	public RawElement strikethrough(boolean strikethrough) {
		texts.get(texts.size()-1).strikethrough(strikethrough);
		return this;
	}
	
	public RawElement onClick(String text, ClickAction action) {
		clickText = new ClickText(text, action);
		return this;
	}
	
	public HoverText hoverText(String text) {
		hoverText = new HoverText(text, this);
		return hoverText;
	}
	
	public RawMessage flipText() {
		return parent;
	}
	
	@Override
	public String toString() {
		
		StringBuilder out = new StringBuilder("\"text\":\"\",\"extra\":[");
		
		for(ColorText text : texts)
			out.append("{").append(text.toString()).append("},");

		out.deleteCharAt(out.length()-1);
		out.append("]");
		
		if(hoverText != null)
			out.append(hoverText.toString());
		if(clickText != null)
			out.append(clickText.toString());
		
		return out.toString();
	}
}