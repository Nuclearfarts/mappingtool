package com.nuclearfarts.mappingtool.cli;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import com.nuclearfarts.mappingtool.mcpmapping.MCPMappings;
import com.nuclearfarts.mappingtool.mcpmapping.parse.CSVFieldMappingParserState;
import com.nuclearfarts.mappingtool.mcpmapping.parse.CSVMethodMappingParserState;
import com.nuclearfarts.mappingtool.mcpmapping.parse.CSVParameterMappingParserState;
import com.nuclearfarts.mappingtool.tsrg.TSRG;
import com.nuclearfarts.mappingtool.tsrg.parse.TSRGBaseParserState;
import com.nuclearfarts.mappingtool.util.mapping.FieldMapping;
import com.nuclearfarts.mappingtool.util.mapping.MethodMapping;
import com.nuclearfarts.mappingtool.util.mapping.ParameterMapping;
import com.nuclearfarts.mappingtool.util.parse.LineSeparatedBaseParserState;
import com.nuclearfarts.mappingtool.util.parse.StatedParser;

public class MappingToolCLI {
	
	public static void main(String[] args) throws IOException {
		switch(args[0]) {
		case "apply":
			apply(args, false);
			break;
		case "reverse":
			apply(args, true);
			break;
		case "applymcp":
			applymcp(args);
			break;
		case "mcp2srg":
			mcp2srg(args);
			break;
		default:
			System.err.println("Usage: java -jar mappingtool.jar <apply|reverse|applymcp> <path/to/srgfile.tsrg|path/to/mcpmappingsdirectory> <path/to/input.jar> <path/to/output.jar> [path/to/inheritance.jar]");
			System.err.println("OR java -jar mappingtool.jar mcp2srg <path/to/mcpmappingsdirectory> <path/to/output.tsrg> <path/to/basesrg.tsrg>");
			System.err.println("for mcp2srg, the base SRG is used to structure the MCP mapping -- it should be an obfuscated to SRG mapping. The output is an SRG to MCP mapping in TSRG format.");
			System.err.println("using applymcp is preferable to using mcp2srg followed by apply, as applymcp supports parameter names.");
			System.err.println("(Inheritance jar is only needed if you're applying mappings to a mod/plugin, not if you're applying them to the minecraft jar.)");
		}
		System.out.println("done");
	}
	
	public static void apply(String[] args, boolean reverse) throws IOException {
		String srgfile = args[1];
		String in = args[2];
		String out = args[3];
		String inheritance = args.length >= 5 ? args[4] : in;
		Files.copy(Paths.get(in), Paths.get(out), StandardCopyOption.REPLACE_EXISTING);
		TSRG mappings = tsrgFrom(Paths.get(srgfile));
		if(reverse) {
			mappings = mappings.reverse();
		}
		mappings.loadInheritance(Paths.get(inheritance));
		mappings.apply(Paths.get(out));
	}
	
	public static void applymcp(String[] args) throws IOException {
		String mappingDir = args[1];
		String in = args[2];
		String out = args[3];
		Path mapPath = Paths.get(mappingDir);
		Files.copy(Paths.get(in), Paths.get(out), StandardCopyOption.REPLACE_EXISTING);
		MCPMappings mcp = mcpFrom(mapPath);
		mcp.apply(Paths.get(out));
	}
	
	public static void mcp2srg(String[] args) throws IOException {
		String mappingDir = args[1];
		String tsrgOut = args[2];
		String baseSrg = args[3];
		TSRG base = tsrgFrom(Paths.get(baseSrg));
		MCPMappings mappings = mcpFrom(Paths.get(mappingDir));
		TSRG newTSRG = mappings.toTSRG(base);
		newTSRG.save(Paths.get(tsrgOut));
	}
	
	private static TSRG tsrgFrom(Path p) throws IOException {
		String tsrg = new String(Files.readAllBytes(p));
		StatedParser<TSRG> parser = new StatedParser<TSRG>(new TSRGBaseParserState());
		return parser.parse(tsrg);
	}
	
	
	private static MCPMappings mcpFrom(Path mapPath) throws IOException {
		String fieldCSV = new String(Files.readAllBytes(mapPath.resolve("fields.csv")));
		List<FieldMapping> fields = new StatedParser<List<FieldMapping>>(new LineSeparatedBaseParserState<FieldMapping>(()-> new CSVFieldMappingParserState())).parse(fieldCSV);
		String methodCSV = new String(Files.readAllBytes(mapPath.resolve("methods.csv")));
		List<MethodMapping> methods = new StatedParser<List<MethodMapping>>(new LineSeparatedBaseParserState<MethodMapping>(()-> new CSVMethodMappingParserState())).parse(methodCSV);
		String paramCSV = new String(Files.readAllBytes(mapPath.resolve("params.csv")));
		List<ParameterMapping> params = new StatedParser<List<ParameterMapping>>(new LineSeparatedBaseParserState<ParameterMapping>(()-> new CSVParameterMappingParserState())).parse(paramCSV);
		return new MCPMappings(fields, methods, params);
	}
}
