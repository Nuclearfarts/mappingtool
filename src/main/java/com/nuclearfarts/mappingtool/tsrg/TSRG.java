package com.nuclearfarts.mappingtool.tsrg;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import com.nuclearfarts.mappingtool.tsrg.mapping.ClassMapping;
import com.nuclearfarts.mappingtool.util.LocalVarClassRemapper;
import com.nuclearfarts.mappingtool.util.PathDepthComparator;

public class TSRG {
	
	public final Map<String, ClassMapping> mappings = new HashMap<String, ClassMapping>();
	private final TSRGBasedRemapper remapper;
	
	public TSRG(Collection<ClassMapping> mappings) {
		remapper = new TSRGBasedRemapper(this);
		mappings.stream().forEach(m -> this.mappings.put(m.getStringIdentifier(), m));
	}
	
	public void apply(Path jar) throws IOException {
		Map<String, String> env = new HashMap<String, String>();
		env.put("create", "true");
		URI uri = URI.create("jar:" + jar.toUri());
		try(FileSystem zipFS = FileSystems.newFileSystem(uri, env)){
			Files.walk(zipFS.getPath("/")).forEach(this::visitFile);
			//Because we changed the contents of the jar (by quite a lot), we need to delete the signature information.
			//I can't be bothered to write something to change the manifest, so we just delete META-INF as a whole.
			//Have to delete the files in the directory before you can delete the directory. 
			//Common approaches to this won't work because it's a ZipFileSystem and therefore can't use File objects.
			//So we sort the stream by reverse path depth and use forEachOrdered.
			if(Files.exists(zipFS.getPath("/META-INF/"))) {
				Files.walk(zipFS.getPath("/META-INF/")).sorted(new PathDepthComparator().reversed()).forEachOrdered((Path p) -> {
					try {
						Files.delete(p);
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
			}
		}
	}
	
	public void loadInheritance(Path jar) throws IOException {
		Map<String, String> env = new HashMap<String, String>();
		env.put("create", "true");
		URI uri = URI.create("jar:" + jar.toUri());
		try(FileSystem zipFS = FileSystems.newFileSystem(uri, env)){
			Files.walk(zipFS.getPath("/")).forEach(this::visitInheritance);
		}
	}
	
	public TSRG reverse() {
		return new TSRG(mappings.values().stream().map(cM -> cM.reverse(remapper)).collect(Collectors.toList()));
	}
	
	private void visitInheritance(Path path) {
		if(path.toString().endsWith(".class")) {
			ClassNode classNode = new ClassNode();
			try(InputStream in = new BufferedInputStream(Files.newInputStream(path))) {
				new ClassReader(in).accept(classNode, 0);
			} catch (IOException e) {
				e.printStackTrace();
			}
			ClassMapping mapping = mappings.get(classNode.name);
			if(mapping != null) {
				ClassMapping superClass = mappings.get(classNode.superName);
				if(superClass != null) {
					mapping.superClass = superClass;
				}
				for(String itf : classNode.interfaces) {
					ClassMapping itfMapping = mappings.get(itf);
					if(itfMapping != null) {
						mapping.interfaces.add(itfMapping);
					}
				}
			}
		}
	}
	
	private void visitFile(Path path) {
		if(path.toString().endsWith(".class")) {
			ClassNode classNode = new ClassNode();
			try(InputStream in = new BufferedInputStream(Files.newInputStream(path))) {
				//System.out.println(path);
				new ClassReader(in).accept(new LocalVarClassRemapper(classNode, remapper, remapper), 0);
				Files.delete(path);
			} catch (IOException e) {
				e.printStackTrace();
			}
			//System.out.println(classNode.name);
			ClassWriter writer = new ClassWriter(0);
			classNode.accept(writer);
			//ClassNode testcn = new ClassNode();
			//new ClassReader(writer.toByteArray()).accept(testcn, 0);
			//System.out.println(testcn.name);
			Path newPath = path.getRoot().resolve(classNode.name + ".class");
			try {
				Files.createDirectories(newPath.getParent());
			} catch (IOException e) {
				e.printStackTrace();
			}
			try(OutputStream out = Files.newOutputStream(newPath)) {
				out.write(writer.toByteArray());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public String toString() {
		return mappings.values().toString();
	}
}
