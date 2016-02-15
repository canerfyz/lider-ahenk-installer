package tr.org.liderahenk.installer.lider.utils;

import java.util.LinkedList;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;

import tr.org.liderahenk.installer.lider.config.LiderSetupConfig;
import tr.org.liderahenk.installer.lider.wizard.LiderSetupWizard;
import tr.org.liderahenk.installer.lider.wizard.pages.IDatabasePage;
import tr.org.liderahenk.installer.lider.wizard.pages.ILdapPage;
import tr.org.liderahenk.installer.lider.wizard.pages.ILiderPage;
import tr.org.liderahenk.installer.lider.wizard.pages.IXmppPage;

public class PageFlowHelper {
	
	/**
	 * Tries to find the first instance of the provided class in the linked
	 * list.
	 * 
	 * @param pagesList
	 * @param cls
	 * @return
	 */
	private static IWizardPage findFirstInstance(LinkedList<IWizardPage> pagesList, Class<?> cls) {
		if (pagesList != null) {
			for (IWizardPage page : pagesList) {
				if (cls.isInstance(page)) {
					return page;
				}
			}
		}
		return null;
	}
	
	/**
	 * This method decides next page according to user's component choices
	 * 
	 * @return
	 */
	public static IWizardPage selectNextPage(LiderSetupConfig config, WizardPage page) {
		LinkedList<IWizardPage> pagesList = ((LiderSetupWizard) page.getWizard()).getPagesList();
		if (page instanceof IDatabasePage) {
			if (config.isInstallLdap()) {
				return findFirstInstance(pagesList, ILdapPage.class);
			} else if (config.isInstallXmpp()) {
				return findFirstInstance(pagesList, IXmppPage.class);
			} else if (config.isInstallLider()){
				return findFirstInstance(pagesList, ILiderPage.class);
			}	
		}
		else if (page instanceof ILdapPage) {
			if (config.isInstallXmpp()) {
				return findFirstInstance(pagesList, ILdapPage.class);
			} else if (config.isInstallLider()){
				return findFirstInstance(pagesList, ILiderPage.class);
			}
		}
		else if (page instanceof IXmppPage) {
			if (config.isInstallLider()) {
				return findFirstInstance(pagesList, ILiderPage.class);
			}
		}
		return null;
	}
}
