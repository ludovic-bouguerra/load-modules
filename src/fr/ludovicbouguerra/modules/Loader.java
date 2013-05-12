package fr.ludovicbouguerra.modules;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.event.EventListenerList;


import fr.ludovicbouguerra.modules.listeners.ModuleListener;

/**
 * Classe qui charge les modules qui se trouvent dans le dossier donn� en param�tre dans le constructeur
 * @author ludovic
 *
 */
public class Loader implements ModuleListener{
	
	/**
	 * Extension d'un fichier .jar
	 */
	private static final String JAREXT = "jar";
	
	/**
	 * Extension d'un fichier class
	 */
	private static final String CLASSEXT = "class";
	
	/**
	 * R�pertoire ou sont install�s les modules
	 */
	private String path;

	/**
	 * garde en m�moire les classes disponibles 
	 */
	private URLClassLoader loader;
	
	/**
	 * On stoque les listeners
	 */
	private EventListenerList listeners = new EventListenerList();
	
	/**
	 * On stoque ici les modules charg�s 
	 */
	private ArrayList<Class> loadedModules = new ArrayList<Class>();
	
		
	/**
	 * @param path : R�pertoire ou se trouvent les modules
	 */
	public Loader(String path){
		this.path = path;
		loader = new URLClassLoader(new URL[0]);
	}
	
	/**
	 * Charge les modules dans le r�pertoire de base
	 * @throws IOException Si le param�tre fourni n'est pas un repertoire ...
	 * @throws IsNotALibraryException 
	 * @throws ClassNotFoundException 
	 */
	public void autoload() throws IOException, ClassNotFoundException 
	{
		
		//-- On liste les fichiers dans le repertoire --
		File directory = new File(path);
		
		
		
		//-- Si c'est un r�pertoire --
		if (directory.isDirectory())
		{
			
			//-- Pour chaque fichier on le charge en m�moire --
			for (File file : directory.listFiles())
			{
				//-- Si le fichier n'est pas un repertoire et que c'est bien une archive jar...
				if (!file.isDirectory() && isJarArchive(file.getName()))
				{
					//-- On charge le module --
					loadModule(file.getCanonicalPath());
					
				}
			}
			
			
			
		}
		else 
		{
			/**
			 * Le repertoire fourni n'en est pas un !
			 */
			throw new IOException();
		}
		
		
	}
	

	/**
	 * Charge une librairie qui se trouve dans le dossier ..
	 * @param filePath : URL de la librairie ...
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	private void loadModule(String filePath) throws IOException, ClassNotFoundException{
		
		try{
			URL url[] = new URL[loader.getURLs().length+1];
			
			for (int i=0; i<loader.getURLs().length; i++){
				
				url[i] = loader.getURLs()[i];
				
			}
			
			url[loader.getURLs().length] = new File(filePath).toURI().toURL();
			
			
			loader = new URLClassLoader(url);
			
			listModuleClass(new JarFile(filePath));
		}
		catch(java.util.zip.ZipException e){
			
		}
		
	}
	
	/**
	 * 
	 * @return la liste des classes existant dans le .jar
	 * @throws ClassNotFoundException 
	 */
	private void listModuleClass(JarFile jar) throws ClassNotFoundException{
		
		
		Enumeration<JarEntry> entries = jar.entries();
		
		while(entries.hasMoreElements()){
			
			JarEntry actuel = entries.nextElement();
			
			if (isClass(actuel.getName()) && (isModuleClass(loader.loadClass(convertClassUrlToPackage(actuel.getName()))))){

				
				fireModuleDetected(loader.loadClass(convertClassUrlToPackage(actuel.getName())));
				
				
			}
			
			
			
		}
		
		
	}
	
	/**
	 * On v�rifie que le fichier porte bien l'extension jar
	 * @param path
	 * @return
	 */
	protected boolean isJarArchive(String path){
		
		return verifExtension(path, JAREXT);
	}
	
	/**
	 * V�rifie que le nom donn� en param�tre est bien une classe
	 * @param name
	 * @return
	 */
	private boolean isClass(String name){
		
		return verifExtension(name, CLASSEXT);
		
	}
	
	/**
	 * Vérifie que le fichier possède bien l'extension donnée 
	 * @param path : Chemin vers le fichier ou nom du fichier
	 * @param ext : Extension a verifier.
	 * @return
	 */
	private boolean verifExtension(String path, String ext){
		String[] file = path.split("\\.");
		
		return (file.length!=0) && (file[file.length-1].equals(ext));
	}
	
	private boolean isModuleClass(Class clazz){
		 
		
		try {
			
			return isModuleInstance(clazz, Module.class);
		} catch (Exception e) {
			return false;
		}
		

		
	}
	
	
	private boolean isModuleInstance(Class module, Class interfac){
		
		return module.isAssignableFrom(interfac);

		
	}
	
	
	private String convertClassUrlToPackage(String dir){
		
		//-- Il faut enlever le .class final --

		return dir.replace("."+CLASSEXT, "").replace("/", ".");
	}
	


	
	public URLClassLoader getUrlClassLoader(){
		return this.loader;
	}
	
	
	public void addModuleListener(ModuleListener listener){
		listeners.add(ModuleListener.class, listener);
		
	}
	
	public void removeModuleListener(ModuleListener listener){
		listeners.remove(ModuleListener.class, listener);
	}
	
	public ModuleListener[] getModuleListener(){
		return listeners.getListeners(ModuleListener.class);
	}
	
	protected void fireModuleDetected(Class module){
		
		for (ModuleListener listener : getModuleListener()){

			listener.newModule(module);

		}
		
	}

	/**
	 * Provient de l'implémentation de ModuleListener
	 * A chaque module détécté, on l'ajoute dans la liste des modules utilisés.
	 */
	public void newModule(Class clazz) {
		
		loadedModules.add(clazz);
		
	}
	
}
