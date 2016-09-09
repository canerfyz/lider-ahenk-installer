package tr.org.pardus.mys.liderahenksetup.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author <a href="mailto:caner.feyzullahoglu@agem.com.tr">Caner
 *         Feyzullahoglu</a>
 * 
 */
public class LiderAhenkUtils {

	/**
	 * @author Caner Feyzullahoğlu <caner.feyzullahoglu@agem.com.tr>
	 * @param str
	 * @return Returns true if parameter <strong><i>str</i></strong> is
	 *         <strong>null</strong> or <strong>"" (empty string)</strong>.
	 */
	public static boolean isEmpty(String str) {
		if (str == null || "".equals(str) || str.isEmpty() || !(str.trim().length() > 0)) {
			return true;
		} else {
			return false;
		}
	}

	public static String replace(Map<String, String> map, String text) {
		for (Entry<String, String> entry : map.entrySet()) {
			text = text.replaceAll(entry.getKey().replaceAll("#", "\\#"), entry.getValue());
		}
		return text;
	}

	/**
	 * Creates file under temporary file directory and writes configuration to
	 * it. Returns absolute path of created temp file.
	 * 
	 * @param content
	 * @param fileName
	 * @return absolute path of created temp file
	 */
	public static String writeToFileReturnPath(String content, String fileName) {

		String absPath = null;

		try {
			File temp = new File(System.getProperty("java.io.tmpdir") + "/" + fileName);

			FileWriter fileWriter = new FileWriter(temp.getAbsoluteFile());

			BufferedWriter buffWriter = new BufferedWriter(fileWriter);

			buffWriter.write(content);
			buffWriter.close();

			absPath = temp.getAbsolutePath();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return absPath;
	}

	public static File streamToFile(InputStream stream, String filename) {
		try {
			File file = new File(System.getProperty("java.io.tmpdir") + File.separator + filename);
			OutputStream outputStream = new FileOutputStream(file);
			int read = 0;
			byte[] bytes = new byte[1024];

			while ((read = stream.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}

			outputStream.close();
			return file;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Creates file under temporary file directory and writes configuration to
	 * it. Returns the created file.
	 * 
	 * @param content
	 * @param fileName
	 * @return created file
	 */
	public static synchronized File writeToFile(String content, String fileName) {

		File tempFile = null;

		try {
			tempFile = new File(System.getProperty("java.io.tmpdir") + File.separator + fileName);

			FileWriter fileWriter = new FileWriter(tempFile.getAbsoluteFile());

			BufferedWriter buffWriter = new BufferedWriter(fileWriter);

			buffWriter.write(content);
			buffWriter.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return tempFile;
	}

}
