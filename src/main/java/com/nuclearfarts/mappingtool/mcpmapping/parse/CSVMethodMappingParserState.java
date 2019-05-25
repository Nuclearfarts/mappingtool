package com.nuclearfarts.mappingtool.mcpmapping.parse;

import java.util.List;

import com.nuclearfarts.mappingtool.tsrg.mapping.MethodMapping;
import com.nuclearfarts.mappingtool.util.parse.ParserState;
import com.nuclearfarts.mappingtool.util.parse.RepeatingParserState;
import com.nuclearfarts.mappingtool.util.parse.TerminatedStringParserState;

public class CSVMethodMappingParserState extends RepeatingParserState<String, MethodMapping> {

	public CSVMethodMappingParserState() {
		super(4);
	}
	
	@Override
	protected ParserState<String> supplyParser(boolean isFinal) {
		if(isFinal) {
			return new TerminatedStringParserState(true, '\n');
		} else {
			return new TerminatedStringParserState(false, ',', '\n');
		}
	}

	@Override
	protected MethodMapping getWith(List<String> results) {
		return new MethodMapping(results.get(0), results.get(1), "");
	}

}
