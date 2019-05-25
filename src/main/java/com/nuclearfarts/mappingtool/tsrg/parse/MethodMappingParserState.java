package com.nuclearfarts.mappingtool.tsrg.parse;

import com.nuclearfarts.mappingtool.tsrg.mapping.MethodMapping;
import com.nuclearfarts.mappingtool.util.parse.ParserState;
import com.nuclearfarts.mappingtool.util.parse.TerminatedStringParserState;

public class MethodMappingParserState extends ParserState<MethodMapping> {

	private int state = 0;
	private final ParserState<String> obfName = new TerminatedStringParserState(false, ' ', '\n');
	private final ParserState<String> desc = new TerminatedStringParserState(false, ' ', '\n');
	private final ParserState<String> srgName = new TerminatedStringParserState(false, ' ', '\n');
	
	@Override
	public ParseOutcome parse(char c) {
		if(Character.isWhitespace(c) && state == 0) {
			return ParseOutcome.CONTINUE;
		}
		switch(state) {
		case 0:
			setNextState(obfName);
			state++;
			return ParseOutcome.SUBSECTION;
		case 1:
			setNextState(desc);
			state++;
			return ParseOutcome.SUBSECTION;
		case 2:
			setNextState(srgName);
			state++;
			return ParseOutcome.SUBSECTION;
		default: //case 2
			return ParseOutcome.RETURN_REPARSE;
		}
	}

	@Override
	public MethodMapping get() {
		return new MethodMapping(obfName.get(), srgName.get(), desc.get());
	}

}
