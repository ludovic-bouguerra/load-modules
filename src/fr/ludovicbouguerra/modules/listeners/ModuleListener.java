package fr.ludovicbouguerra.modules.listeners;

import java.util.EventListener;

public interface ModuleListener extends EventListener{
	
	/**
	 * Lorsqu'un module est detect� renvoit la classe du module 
	 */
	void newModule(Class clazz);
}
