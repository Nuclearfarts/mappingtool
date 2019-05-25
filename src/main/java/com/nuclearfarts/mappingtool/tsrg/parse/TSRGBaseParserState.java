package com.nuclearfarts.mappingtool.tsrg.parse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.nuclearfarts.mappingtool.tsrg.TSRG;
import com.nuclearfarts.mappingtool.util.parse.ParserState;

public class TSRGBaseParserState extends ParserState<TSRG> {
	
	private final List<ClassMappingParserState> classes = new ArrayList<ClassMappingParserState>();
	
	@Override
	public ParseOutcome parse(char c) {
		return ParseOutcome.SUBSECTION;
	}
	
	@Override
	public ParserState<?> getNewState() {
		ClassMappingParserState mapping = new ClassMappingParserState();
		classes.add(mapping);
		return mapping;
	}

	@Override
	public TSRG get() {
		//System.out.println(classes);
		return new TSRG(classes.stream().map(cP -> cP.get()).collect(Collectors.toList()));
	}

}
