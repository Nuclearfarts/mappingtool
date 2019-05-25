package com.nuclearfarts.mappingtool.util.parse;

import java.util.ArrayDeque;
import java.util.Deque;

import com.nuclearfarts.mappingtool.util.parse.ParserState.ParseOutcome;

public class StatedParser<T> {
	
	protected final Deque<ParserStackEntry> parserStack = new ArrayDeque<ParserStackEntry>();
	public final ParserState<T> root;
	
	public StatedParser(ParserState<T> start) {
		root = start;
	}
	
	public T parse(String in) {
		ParserState<?> current = root;
		ParseOutcome outcome = null;
		char c = 0;
		int pos = 0;
		int secStart = 0;
		boolean done = false;
		while(!done && pos < in.length()) {
			c = (char)in.charAt(pos);
			
			outcome = current.parse(c);
			//System.out.print(c);
			//System.out.println("parser: " + current + " char: "+ c + " outcome: " + outcome + "pos: " + pos);
			switch(outcome) {
			case RETURN:
				pos++;
			case RETURN_REPARSE:
				if(!parserStack.isEmpty()) {
					ParserStackEntry stackE = parserStack.pop();
					secStart = stackE.secStart;
					//System.out.println(outcome.toString() + current);
					current = stackE.state;
				} else {
					done = true;
				}
				break;
			case SUBSECTION:
				//System.out.println(outcome.toString() + current);
				parserStack.push(new ParserStackEntry(secStart, current));
				secStart = pos;
				current = current.getNewState();
				break;
			case RESTART_WITH_NEW_STATE:
				pos = secStart;
				current = current.getNewState();
				break;
			case CONTINUE:
				pos++;
				break;
			}
		}
		return root.get();
	}
	
	protected static class ParserStackEntry {
		
		public final int secStart;
		public final ParserState<?> state;
		
		protected ParserStackEntry(int sectionStart, ParserState<?> state) {
			this.secStart = sectionStart;
			this.state = state;
		}
	}
}
