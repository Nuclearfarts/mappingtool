package com.nuclearfarts.mappingtool.tsrg.parse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.nuclearfarts.mappingtool.util.mapping.ClassMapping;
import com.nuclearfarts.mappingtool.util.mapping.FieldMapping;
import com.nuclearfarts.mappingtool.util.mapping.MethodMapping;
import com.nuclearfarts.mappingtool.util.parse.ParserState;
import com.nuclearfarts.mappingtool.util.parse.TerminatedStringParserState;

public class ClassMappingParserState extends ParserState<ClassMapping> {
	
	private int state = 0;
	private final ParserState<String> obfName = new TerminatedStringParserState(false, ' ', '\n');
	private final ParserState<String> srgName = new TerminatedStringParserState(false, ' ', '\n');
	private final List<MethodMappingParserState> methods = new ArrayList<MethodMappingParserState>();
	private final List<FieldMappingParserState> fields = new ArrayList<FieldMappingParserState>();
	
	@Override
	public ParseOutcome parse(char c) {
		switch(state) {
		case 0:
			setNextState(obfName);
			state++;
			return ParseOutcome.SUBSECTION;
		case 1:
			setNextState(srgName);
			state++;
			return ParseOutcome.SUBSECTION;
		default:
			if(Character.isWhitespace(c)) {
				setNextState(new MemberMappingParserState(this::supplyMethodMappingParser, this::supplyFieldMappingParser));
				return ParseOutcome.SUBSECTION;
			} else {
				return ParseOutcome.RETURN_REPARSE;
			}
		}
	}
	
	private MethodMappingParserState supplyMethodMappingParser() {
		MethodMappingParserState method = new MethodMappingParserState();
		methods.add(method);
		return method;
	}
	
	private FieldMappingParserState supplyFieldMappingParser() {
		FieldMappingParserState field = new FieldMappingParserState();
		fields.add(field);
		return field;
	}
	
	@Override
	public ClassMapping get() {
		List<MethodMapping> methodMappings = methods.stream().map(mP -> mP.get()).collect(Collectors.toList());
		List<FieldMapping> fieldMappings = fields.stream().map(fP -> fP.get()).collect(Collectors.toList());
		return new ClassMapping(obfName.get(), srgName.get(), fieldMappings, methodMappings);
	}
}
