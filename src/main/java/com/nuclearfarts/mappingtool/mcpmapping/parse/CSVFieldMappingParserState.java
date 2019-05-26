package com.nuclearfarts.mappingtool.mcpmapping.parse;

import java.util.List;

import com.nuclearfarts.mappingtool.util.mapping.FieldMapping;
import com.nuclearfarts.mappingtool.util.parse.ParserState;
import com.nuclearfarts.mappingtool.util.parse.RepeatingParserState;
import com.nuclearfarts.mappingtool.util.parse.TerminatedStringParserState;

public class CSVFieldMappingParserState extends RepeatingParserState<String, FieldMapping> {
	
	public CSVFieldMappingParserState() {
		super(4);
	}
	
	@Override
	protected ParserState<String> supplyParser(boolean isFinal) {
		//System.out.println(isFinal);
		if(isFinal) {
			return new TerminatedStringParserState(true, '\n');
		} else {
			return new TerminatedStringParserState(false, ',', '\n');
		}
	}

	@Override
	protected FieldMapping getWith(List<String> results) {
		return new FieldMapping(results.get(0), results.get(1));
	}
}
