package br.com.lhs.pontocontrol.web;

import org.apache.commons.io.FilenameUtils;

public class WebMainClass {

	public static String getRoot() {
		return FilenameUtils.getFullPath(WebMainClass.class.getProtectionDomain().getCodeSource().getLocation().getFile());
	}

}
