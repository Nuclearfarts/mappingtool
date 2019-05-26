package com.nuclearfarts.mappingtool.tsrg.parse;

import com.nuclearfarts.mappingtool.util.mapping.FieldMapping;
import com.nuclearfarts.mappingtool.util.parse.ParserState;
import com.nuclearfarts.mappingtool.util.parse.TerminatedStringParserState;

public class FieldMappingParserState extends ParserState<FieldMapping> {
	
	private int state = 0;
	private final ParserState<String> obfName = new TerminatedStringParserState(false, ' ', '\n');
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
			setNextState(srgName);
			state++;
			return ParseOutcome.SUBSECTION;
		default: //case 2
			return ParseOutcome.RETURN_REPARSE;
		}
	}

	@Override
	public FieldMapping get() {
		return new FieldMapping(obfName.get(), srgName.get());
	}

}
