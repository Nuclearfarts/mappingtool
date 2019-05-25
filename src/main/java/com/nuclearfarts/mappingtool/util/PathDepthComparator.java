package com.nuclearfarts.mappingtool.util;

import java.nio.file.Path;
import java.util.Comparator;

public class PathDepthComparator implements Comparator<Path> {

	@Override
	public int compare(Path o1, Path o2) {
		return o1.getNameCount() - o2.getNameCount();
	}

}
