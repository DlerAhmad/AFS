import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Scanner;
import java.util.Set;

import static java.nio.file.StandardCopyOption.*;

public class FileOperations {


	public static String list(String path){
		String result="";
		Path file1 = Paths.get(path);
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(file1)) {
			for (Path file: stream) {
				result=result+(file.getFileName().toString())+"\n";
			}
		} catch (IOException | DirectoryIteratorException x) {
			// IOException can never be thrown by the iteration.
			// In this snippet, it can only be thrown by newDirectoryStream.
			//System.err.println(x);
			return "NO";
		}
		return result;
	}
	public static void move(String sour,String tar){
		//Scanner input = new Scanner(System.in);
		//System.out.println("Enter the source and target folder");
		Path source;
		Path target;

		source = Paths.get(sour);
		target = Paths.get(tar);
		try {
			Files.move(source, target,REPLACE_EXISTING);
		} catch (IOException e) {
			
		}
		//input.close();
		
	}

	public static void copy(String sour,String targ){
		Scanner input = new Scanner(System.in);
		//System.out.println("Enter the source and target path");
		Path source;
		Path target;


		source = Paths.get(sour);
		target = Paths.get(targ);

		
			// Performs the copy even when the target file already
			// exists.
			// If the target is a symbolic link, the link itself is
			// copied
			// (and not the target of the link). If the target is a
			// non-empty directory,
			// the copy fails with the FileAlreadyExistsException
			// exception.

			try {
				Files.copy(source, target,REPLACE_EXISTING);
			
			} catch (IOException e) {
			
				
			}
		
		//input.close();
	}

	public static void append(String path,String input) throws IOException {


		
		FileWriter fw = new FileWriter(path,false);
		try {

			fw.write(input);


		} catch (IOException e) {
			
			// TODO Auto-generated catch block
			//e.printStackTrace();
		} //the true will append the new data

		fw.close();
	
	}


	public static String append(String path){

		String token;
		String contents="";
		Scanner scan = new Scanner(System.in);

		//FileWriter fw = new FileWriter(path,true); //the true will append the new data

		while(scan.hasNextLine()){
			token=scan.nextLine();
			if(!token.equals("close")){
				contents=contents+token+"\n";
				
			}
			else
			{
				break;
			}
		}

		return contents;
	}


	public static String open(String path){
		String output="";
		try {
			Scanner fileIn = new Scanner(new File(path));
			while(fileIn.hasNextLine())
			{
				//output =output+"\n"+fileIn.nextLine();
				output =output+fileIn.nextLine();
				//System.out.println("File contents:"+output);
				if(fileIn.hasNextLine()){
					output=output+"\n";
				}
			}
		} catch (FileNotFoundException e) {
			
			create(Paths.get(path));
			return "NO";
		}
		return output;
	}

	public static int create(Path file) {
		try {
			// Create the empty file with default permissions, etc.
			Files.createFile(file);
		} catch (FileAlreadyExistsException x) {
			//System.err.format("file named %s" + " already exists%n", file);
			return -1;
		} catch (IOException x) {
			// Some other sort of failure, such as permissions.
			System.err.format("createFile error: %s%n", x);
		}
		return 0;
	}
	public static int createDir(String path) {
		Path file=Paths.get(path);
		try {
			// Create the empty file with default permissions, etc.
			Files.createDirectory(file);
		} catch (FileAlreadyExistsException x) {
			//System.err.format("file named %s" + " already exists%n", file);
			return -1;
		} catch (IOException x) {
			// Some other sort of failure, such as permissions.
		//	System.err.format("createFile error: %s%n", x);
		}
		return 0;
	}

	public static int delete(String path) {

		Path file=Paths.get(path);
		
		try {

			Files.delete(file);
		
		} catch (NoSuchFileException x) {
		//	System.err.format("%s: no such" + " file or directory%n", file);
			return -2;
		} catch (DirectoryNotEmptyException x) {
			//System.err.format("%s not empty%n", file);
			return -1;
		} catch (IOException x) {
			// File permission problems are caught here.
			System.err.println(x);
		}
		return 0;
	}

}


