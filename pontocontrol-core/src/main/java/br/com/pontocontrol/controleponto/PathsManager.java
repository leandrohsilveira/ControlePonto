/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.pontocontrol.controleponto;

import java.io.File;

import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author silveira
 */
public final class PathsManager {

	private Class<?> mainClass;

	protected PathsManager(Class<?> mainClass) {
		this.mainClass = mainClass;
	}

	protected static PathsManager instance;

	public static PathsManager getInstance() {
		if(instance == null) {
			throw new IllegalStateException("A aplicação deve ser configurada antes. SEE: "+ApplicationFactory.class.getName());
		}
		return instance;
	}

	public File getFile(String path, String res) {
		return getFileResource(path, res);
	}

	public String getPathUsuario(String login) {
		return String.format("%s/%s", projectDataPath(), login);
	}

	public String projectDataPath() {
		return String.format("%s/data", projectRootPath());
	}

	public String projectImagesPath() {
		return String.format("%s/img", projectRootPath());
	}

	public File getImageFile(String resource) {
		return getFileResource(projectImagesPath(), resource);
	}

	public String projectBinPath() {
		return String.format("%s/bin", projectRootPath());
	}

	public File getFileResource(String path, String resource) {
		return new File(String.format("%s/%s", path, resource));
	}

	public String projectRootPath() {
		String buildDir = FilenameUtils
				.getFullPath(mainClass.getProtectionDomain().getCodeSource().getLocation().getFile());
		return new File(buildDir).getParentFile().getAbsolutePath();
	}
}
