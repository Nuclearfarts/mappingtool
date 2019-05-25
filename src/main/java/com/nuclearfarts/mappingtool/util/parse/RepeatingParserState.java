package com.nuclearfarts.mappingtool.util.parse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class RepeatingParserState<T, R> extends ParserState<R> {

	private int timesRemaining;
	private final List<ParserState<T>> parsers = new ArrayList<ParserState<T>>();
	
	public RepeatingParserState(int times) {
		this.timesRemaining = times - 1;
	}
	
	protected abstract ParserState<T> supplyParser(boolean isFinal);
	
	protected abstract R getWith(List<T> results);
	
	@Override
	public ParseOutcome parse(char c) {
		if(timesRemaining > 0) {
			timesRemaining--;
			return ParseOutcome.SUBSECTION;
		} else {
			return ParseOutcome.RETURN_REPARSE;
		}
	}
	
	@Override
	public ParserState<T> getNewState() {
		ParserState<T> parser = supplyParser(timesRemaining == 0);
		parsers.add(parser);
		return parser;
	}
	
	
	@Override
	public R get() {
		return getWith(parsers.stream().map(p -> p.get()).collect(Collectors.toList()));
	}

}
