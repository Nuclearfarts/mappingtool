package com.nuclearfarts.mappingtool.util.parse;

import java.util.LinkedList;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ParserStateChain<T> extends ParserState<T[]> {
	
	private final Queue<ParserState<T>> parserQueue = new LinkedList<ParserState<T>>();
	private final ParserState<T>[] parsers;
	
	@SafeVarargs
	public ParserStateChain(ParserState<T>... parserStates) {
		this.parsers = parserStates;
		for(ParserState<T> state : parserStates) {
			parserQueue.add(state);
		}
	}
	
	@Override
	public ParseOutcome parse(char c) {
		return parserQueue.isEmpty() ? ParseOutcome.RETURN : ParseOutcome.SUBSECTION;
	}
	
	@Override
	public ParserState<T> getNewState() {
		return parserQueue.poll();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T[] get() {
		return (T[]) Stream.of(parsers).map(p -> p.get()).collect(Collectors.toList()).toArray();
	}

}
