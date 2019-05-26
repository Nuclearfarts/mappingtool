package com.nuclearfarts.mappingtool.util.parse;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class LineSeparatedBaseParserState<T> extends ParserState<List<T>> {
	
	private boolean sepVerified = true;
	private final char lineSep;
	private final Supplier<ParserState<T>> parserFactory;
	private final List<ParserState<T>> parsers = new ArrayList<ParserState<T>>();
	
	public LineSeparatedBaseParserState(Supplier<ParserState<T>> parserFactory, char lineSeparator) {
		this.parserFactory = parserFactory;
		this.lineSep = lineSeparator;
	}
	
	public LineSeparatedBaseParserState(Supplier<ParserState<T>> parserFactory) {
		this(parserFactory, '\n');
	}
	
	@Override
	public ParseOutcome parse(char c) {
		if(c == lineSep) {
			sepVerified = true;
			return ParseOutcome.CONTINUE;
		} else if(sepVerified) {
			sepVerified = false;
			return ParseOutcome.SUBSECTION;
		} else {
			System.out.println(c);
			System.out.println(parsers.get(parsers.size() - 1).get());
			throw new RuntimeException("Line " + parsers.size() + " parser returned early or late. Do not eat the newline!");
		}
	}
	
	@Override
	public ParserState<T> getNewState() {
		ParserState<T> parser = parserFactory.get();
		parsers.add(parser);
		//System.out.println(parser);
		return parser;
	}

	@Override
	public List<T> get() {
		//Null check removes the trailing newline error.
		return parsers.stream().map(p -> p.get()).filter(p -> p != null).collect(Collectors.toList());
	}

}
