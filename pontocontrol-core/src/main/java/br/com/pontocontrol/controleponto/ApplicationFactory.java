package br.com.pontocontrol.controleponto;

public abstract class ApplicationFactory {


	public static void runApplication(Class<?> mainClass) {
		if(PathsManager.instance == null) {
			PathsManager.instance = new PathsManager(mainClass);
		} else {
			throw new IllegalStateException("Você deve configurar a aplicação apenas uma vez!");
		}

	}

}
