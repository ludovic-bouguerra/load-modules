package fr.ludovicbouguerra.modules;

/**
 * Interface module qui doit être implémentée par tous les modules ...
 * @author ludovic
 *
 */
public interface Module {

	
	
	/**
	 * 
	 * @return le nom du module
	 */
	public String getName();
	
	/**
	 * 
	 * @return la version du module
	 */
	public String getVersion();
	
	/**
	 * Retourne le nom de l'auteur
	 */
	public String getAuthor();
	
	
	
}
