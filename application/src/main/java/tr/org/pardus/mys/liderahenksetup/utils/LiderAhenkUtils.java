package tr.org.pardus.mys.liderahenksetup.utils;

public class LiderAhenkUtils {

	/**
	 * @author Caner Feyzullahoğlu <caner.feyzullahoglu@agem.com.tr>
	 * @param str
	 * @return Returns true if parameter 
	 * <strong><i>str</i></strong> is 
	 * <strong>null</strong> or 
	 * <strong>"" (empty string)</strong>.
	 */
	public static boolean isEmpty(String str) {
		if (str == null || "".equals(str) || 
				str.isEmpty() || !(str.trim().length() > 0)) {
			return true;
		}
		else {
			return false;
		}
	}
}
