package com.nuclearfarts.mappingtool.tsrg.parse;

import java.util.function.Supplier;

import com.nuclearfarts.mappingtool.util.mapping.MemberMapping;
import com.nuclearfarts.mappingtool.util.parse.ParserState;

public class MemberMappingParserState extends ParserState<MemberMapping> {
	
	private boolean primed = false;
	private final Supplier<MethodMappingParserState> methodCallback;
	private final Supplier<FieldMappingParserState> fieldCallback;
	
	public MemberMappingParserState(Supplier<MethodMappingParserState> methodCallback, Supplier<FieldMappingParserState> fieldCallback) {
		this.methodCallback = methodCallback;
		this.fieldCallback = fieldCallback;
	}
	
	@Override
	public ParseOutcome parse(char c) {
		if(c == ' ') {
			primed = true;
		} else if(primed) {
			if(c == '(') {
				setNextState(methodCallback.get());
			} else {
				setNextState(fieldCallback.get());
			}
			return ParseOutcome.RESTART_WITH_NEW_STATE;
		}
		return ParseOutcome.CONTINUE;
	}

	@Override
	public MemberMapping get() {
		return (MemberMapping) newState.get();
	}

}
