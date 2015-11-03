import java.io.File;
import java.io.IOException;

public class NameTest {

	public static void main(String[] args) throws IOException {

		String[] fileArray = { 
				"C:\\projects\\workspace\\testing\\f1\\f2\\f3\\file5.txt", 
				"folder/f ile3.txt",
				"../testing/file1.txt", 
				"../testing", 
				"f1/f2"
		};

		for (String f : fileArray) {
			displayInfo(f);
		}

	}

	public static void displayInfo(String f) throws IOException {
		File file = new File(f);
		System.out.println("========================================");
		System.out.println("          name:" + file.getName());
		System.out.println("  is directory:" + file.isDirectory());
		System.out.println("        exists:" + file.exists());
		System.out.println("          path:" + file.getPath());
		System.out.println(" absolute file:" + file.getAbsoluteFile());
		System.out.println(" absolute path:" + file.getAbsolutePath());
		System.out.println("canonical file:" + file.getCanonicalFile());
		System.out.println("canonical path:" + file.getCanonicalPath());
	}

}
