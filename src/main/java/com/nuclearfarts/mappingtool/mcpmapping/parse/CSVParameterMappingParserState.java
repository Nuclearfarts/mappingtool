package com.nuclearfarts.mappingtool.mcpmapping.parse;

import java.util.List;

import com.nuclearfarts.mappingtool.tsrg.mapping.ParameterMapping;
import com.nuclearfarts.mappingtool.util.parse.ParserState;
import com.nuclearfarts.mappingtool.util.parse.RepeatingParserState;
import com.nuclearfarts.mappingtool.util.parse.TerminatedStringParserState;

public class CSVParameterMappingParserState extends RepeatingParserState<String, ParameterMapping> {

	public CSVParameterMappingParserState() {
		super(3);
	}

	public ParserState<String> supplyParser(boolean isFinal) {
		if(isFinal) {
			return new TerminatedStringParserState(true, '\n');
		} else {
			return new TerminatedStringParserState(false, ',', '\n');
		}
	}

	@Override
	protected ParameterMapping getWith(List<String> results) {
		return new ParameterMapping(results.get(0), results.get(1));
	}

}
