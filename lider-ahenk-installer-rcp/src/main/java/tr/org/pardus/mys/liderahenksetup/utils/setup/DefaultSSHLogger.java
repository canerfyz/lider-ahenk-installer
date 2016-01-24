package tr.org.pardus.mys.liderahenksetup.utils.setup;

import java.util.Hashtable;

import tr.org.pardus.mys.liderahenksetup.utils.PropertyReader;

public class DefaultSSHLogger implements com.jcraft.jsch.Logger {

	public static Hashtable<Integer, String> name = null;

	static {
		name = new Hashtable<Integer, String>();
		name.put(new Integer(DEBUG), "DEBUG: ");
		name.put(new Integer(INFO), "INFO: ");
		name.put(new Integer(WARN), "WARN: ");
		name.put(new Integer(ERROR), "ERROR: ");
		name.put(new Integer(FATAL), "FATAL: ");
	}

	@Override
	public boolean isEnabled(int level) {
		Integer val = PropertyReader.propertyInt("ssh.logger.level");
		return val == null ? true : (val.intValue() == level);
	}

	@Override
	public void log(int level, String message) {
		// TODO
		System.err.print(name.get(new Integer(level)));
		System.err.println(message);
	}

}
