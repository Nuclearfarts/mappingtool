package com.nuclearfarts.mappingtool.util.parse;

public class TerminatedStringParserState extends ParserState<String> {
	
	private final char[] separators;
	private final StringBuilder builder = new StringBuilder();
	private final ParseOutcome ret;
	
	public TerminatedStringParserState(boolean reparse, char... separators) {
		this.separators = separators;
		ret = reparse ? ParseOutcome.RETURN_REPARSE : ParseOutcome.RETURN;
	}
	
	@Override
	public ParseOutcome parse(char c) {
		if(checkChar(c)) {
			return ret;
		} else {
			builder.append(c);
			return ParseOutcome.CONTINUE;
		}
	}

	private boolean checkChar(char c) {
		for(char sep : separators) {
			if(sep == c) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String get() {
		return builder.toString();
	}

}
