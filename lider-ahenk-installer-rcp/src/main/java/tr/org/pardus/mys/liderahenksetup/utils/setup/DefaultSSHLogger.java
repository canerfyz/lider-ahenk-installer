package tr.org.pardus.mys.liderahenksetup.utils.setup;

import java.util.Hashtable;

import tr.org.pardus.mys.liderahenksetup.utils.PropertyReader;

/**
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * 
 */
public class DefaultSSHLogger implements com.jcraft.jsch.Logger {

	private static Hashtable<Integer, String> name = null;
	static {
		name = new Hashtable<Integer, String>();
		name.put(Integer.valueOf(DEBUG), "DEBUG: ");
		name.put(Integer.valueOf(INFO), "INFO: ");
		name.put(Integer.valueOf(WARN), "WARN: ");
		name.put(Integer.valueOf(ERROR), "ERROR: ");
		name.put(Integer.valueOf(FATAL), "FATAL: ");
	}

	@Override
	public boolean isEnabled(int level) {
		Integer val = PropertyReader.propertyInt("ssh.logger.level");
		return val == null ? true : (val.intValue() == level);
	}

	@Override
	public void log(int level, String message) {
		// TODO
		System.err.print(name.get(Integer.valueOf(level)));
		System.err.println(message);
	}

}
