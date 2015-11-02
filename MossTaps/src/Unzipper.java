import java.io.File;
import java.util.Collection;
import net.lingala.zip4j.exception.ZipException;

import net.lingala.zip4j.core.ZipFile;

import org.apache.commons.io.FileUtils;


public class Unzipper {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Collection<File> files = FileUtils.listFiles(
				new File("C:/Users/Dana/Documents/GitHub/MOSS-TAPS/MossTaps/data/Original"),
				new String[] { "zip" }, true);
		showFiles(files);
		for (File file : files) {
			unzip(file.getPath(),file.getParent());
		}

	}
	private static void unzip(String source, String destination) {
		try {
			ZipFile zipFile = new ZipFile(source);
			zipFile.extractAll(destination);
		} catch (ZipException e) {
			e.printStackTrace();
		}
		
	}
	private static void showFiles(Collection<File> files) {
		for (File file : files) {
			System.out.println(file.getAbsolutePath());
		}
		
	}

}
