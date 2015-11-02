import java.io.File;
import java.util.Collection;
import java.net.URL;
import org.apache.commons.io.FileUtils;
import it.zielke.moji.SocketClient;

public class QuickStart {
    public static void main(String[] args) throws Exception {
        // a list of students' source code files located in the prepared
        // directory.
        Collection<File> files = FileUtils.listFiles(new File(
//            "C:/Users/Dana/Documents/GitHub/MOSS-TAPS/MossTaps/data"), new String[] { "java" }, true);
        	"C:/Users/Dana/Documents/GitHub/MOSS-TAPS/MossTaps/data/Java_Students"), new String[] { "java" }, true);
        showFiles(files);

        // a list of base files that was given to the students for this
        // assignment.
        Collection<File> baseFiles = FileUtils.listFiles(new File(
//            "C:/Users/Dana/Documents/GitHub/MOSS-TAPS/MossTaps/data/base_directory"), new String[] { "java" }, true);
    		"C:/Users/Dana/Documents/GitHub/MOSS-TAPS/MossTaps/data/base_directory"), new String[] { "java" }, true);
        showFiles(baseFiles);

        //get a new socket client to communicate with the Moss server
        //and set its parameters.
        SocketClient socketClient = new SocketClient();

        //set your Moss user ID
        socketClient.setUserID("260284329");
        //socketClient.setOpt...

        //set the programming language of all student source codes
        socketClient.setLanguage("java");

        //initialize connection and send parameters
        System.out.println("starting SocketClient");
        socketClient.run();

        // upload all base files
        System.out.println("uploading basefiles");
        for (File f : baseFiles) {
            socketClient.uploadBaseFile(f);
        }

        //upload all source files of students
        System.out.println("uploading sourcefiles");
        for (File f : files) {
            socketClient.uploadFile(f);
        }

        //finished uploading, tell server to check files
        System.out.println("sending query");
        socketClient.sendQuery();

        //get URL with Moss results and do something with it
        URL results = socketClient.getResultURL();
        System.out.println("Results available at " + results.toString());
    }

	private static void showFiles(Collection<File> files) {
		for (File file : files) {
			System.out.println(file.getAbsolutePath());
		}
		
	}
}