package com.nuclearfarts.mappingtool.util.parse;

public abstract class ParserState<T> {
	
	protected ParserState<?> newState = null;
	
	protected void setNextState(ParserState<?> state) {
		newState = state;
	}
	
	public abstract ParseOutcome parse(char c);
	
	public ParserState<?> getNewState() {
		return newState;
	}
	
	public abstract T get();
	
	public enum ParseOutcome {
		RETURN,
		CONTINUE,
		SUBSECTION,
		RESTART_WITH_NEW_STATE,
		RETURN_REPARSE;
	}
}
