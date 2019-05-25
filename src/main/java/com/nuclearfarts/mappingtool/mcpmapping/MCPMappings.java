package com.nuclearfarts.mappingtool.mcpmapping;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import com.nuclearfarts.mappingtool.tsrg.LocalVarClassRemapper;
import com.nuclearfarts.mappingtool.tsrg.mapping.FieldMapping;
import com.nuclearfarts.mappingtool.tsrg.mapping.MethodMapping;
import com.nuclearfarts.mappingtool.tsrg.mapping.ParameterMapping;
import com.nuclearfarts.mappingtool.util.PathDepthComparator;

public class MCPMappings {

	public final Map<String, FieldMapping> fields = new HashMap<String, FieldMapping>();
	public final Map<String, MethodMapping> methods = new HashMap<String, MethodMapping>();
	public final Map<String, ParameterMapping> parameters = new HashMap<String, ParameterMapping>();
	public final MCPMappingBasedRemapper remapper = new MCPMappingBasedRemapper(this);

	public MCPMappings(List<FieldMapping> fields, List<MethodMapping> methods, List<ParameterMapping> parameters) {
		fields.stream().forEach(m -> this.fields.put(m.getStringIdentifier(), m));
		methods.stream().forEach(m -> this.methods.put(m.getStringIdentifier(), m));
		parameters.stream().forEach(m -> this.parameters.put(m.getStringIdentifier(), m));
	}

	public void apply(Path jar) throws IOException {
		Map<String, String> env = new HashMap<String, String>();
		env.put("create", "true");
		URI uri = URI.create("jar:" + jar.toUri());
		try (FileSystem zipFS = FileSystems.newFileSystem(uri, env)) {
			Files.walk(zipFS.getPath("/")).forEach(this::visitFile);
			// Because we changed the contents of the jar (by quite a lot), we need to
			// delete the signature information.
			// I can't be bothered to write something to change the manifest, so we just
			// delete META-INF as a whole.
			// Have to delete the files in the directory before you can delete the
			// directory.
			// Common approaches to this won't work because it's a ZipFileSystem and
			// therefore can't use File objects.
			// So we sort the stream by reverse path depth and use forEachOrdered.
			if (Files.exists(zipFS.getPath("/META-INF/"))) {
				Files.walk(zipFS.getPath("/META-INF/")).sorted(new PathDepthComparator().reversed())
						.forEachOrdered((Path p) -> {
							try {
								Files.delete(p);
							} catch (IOException e) {
								e.printStackTrace();
							}
						});
			}
		}
	}

	private void visitFile(Path path) {
		if (path.toString().endsWith(".class")) {
			ClassNode classNode = new ClassNode();
			try (InputStream in = new BufferedInputStream(Files.newInputStream(path))) {
				// System.out.println(path);
				new ClassReader(in).accept(new LocalVarClassRemapper(classNode, remapper, remapper), 0);
				Files.delete(path);
			} catch (IOException e) {
				e.printStackTrace();
			}
			// System.out.println(classNode.name);
			ClassWriter writer = new ClassWriter(0);
			classNode.accept(writer);
			// ClassNode testcn = new ClassNode();
			// new ClassReader(writer.toByteArray()).accept(testcn, 0);
			// System.out.println(testcn.name);
			Path newPath = path.getRoot().resolve(classNode.name + ".class");
			try {
				Files.createDirectories(newPath.getParent());
			} catch (IOException e) {
				e.printStackTrace();
			}
			try (OutputStream out = Files.newOutputStream(newPath)) {
				out.write(writer.toByteArray());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
