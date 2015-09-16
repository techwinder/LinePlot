package techwind.util.file;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.util.Log;


public class FilesFinder 
{
//	private static final String TAG = "f3fTime/FilesFinder";
	
	public FilesFinder() 
	{
		super();
	}

	public void findFiles(String directoryPath, ArrayList<String> FileArray) 
	{
		File directory = new File(directoryPath);
		FileArray.clear();
		
		if(!directory.exists())
		{
//			Log.i(TAG, "The directory '"+directoryPath+" does not exist");
		}
		else if(!directory.isDirectory())
		{
//			Log.i(TAG, "The path "+directoryPath+" is a file and not a directory");
		}
		else
		{
			File[] subfiles = directory.listFiles();
			String message = "The directory "+directoryPath+" contains "+ subfiles.length+" files"+(subfiles.length>1?"s":"");
//			Log.i(TAG, message);
			for(int i=0 ; i<subfiles.length; i++)
			{
				FileArray.add(subfiles[i].getName());
			}
			FileArray.trimToSize();
			Collections.sort(FileArray, new SortBasedOnName());
		}
	}

	
	static public class SortBasedOnName implements Comparator
	{
		public int compare(Object o1, Object o2)
		{
			String str1 = (String)o1;
			String str2 = (String)o2;
			return str1.compareToIgnoreCase(str2);
		}
	}
}