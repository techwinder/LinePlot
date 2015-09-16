/****************************************************************************

	Globals Class
	Copyright (C) 2013 Andre Deperrois adeperrois@xflr5.com

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

*****************************************************************************/


package techwind.util.file;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import android.os.Environment;
import android.util.Log;

public class Globals
{
	static private final String TAG="Globals";
	static public File s_ExportDir;         /** the directory for all data*/ 

	/** Creates the folders on the Android device in which will be stored the output files
	 *  The files are created in the external storage device, formerly the SDCard in 
	 *  previous Android versions.
	*/
	static public void setupDataDir(String pathName)
	{
		if(pathName.length()>0)
		{
			s_ExportDir = new File(pathName);			
		}
		else
		{
			// the preferred solution is to store on the sdcard, if there is one
			boolean bExternalStorageAvailable = false;
			boolean bExternalStorageWriteable = false;
			String state = Environment.getExternalStorageState();
	
			if (Environment.MEDIA_MOUNTED.equals(state)) 
			{
			    // We can read and write the media
			    bExternalStorageAvailable = bExternalStorageWriteable = true;
			} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) 
			{
			    // We can only read the media
			    bExternalStorageAvailable = true;
			    bExternalStorageWriteable = false;
			} 
			else 
			{
			    // Something else is wrong. It may be one of many other states, but all we need
			    //  to know is we can neither read nor write
			    bExternalStorageAvailable = bExternalStorageWriteable = false;
			}
	
			if(bExternalStorageAvailable && bExternalStorageWriteable)
			{
				//there is no method to access directly the root of the sdcard 
				File DCIMDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString());
				File ExportDir = new File(DCIMDir.getParent());			
				if (!ExportDir.exists() || !ExportDir.isDirectory())
				{
					ExportDir.mkdir();
				}
				s_ExportDir = ExportDir;
			}
			else
			{
				//give up
			}
		}
	}
	
	
	
	/** Checks if the file exists, if not creates it
	 @param fileName :  the name of the file to create
	 */
	static void createFile(String fileName)
	{
		File newFile = new File(fileName);
		if(!newFile.exists())
		{
			try 
			{
				newFile.createNewFile();
			}
			catch (IOException e) 
			{
				Log.i(TAG,"Could not create the file:"+newFile);
			}
		}		
	}
	
	/**Empties the file's content
	 @param fileName :  the name of the file the content of which shall be cleared
	 */
	static public void clearFile(String fileName)
	{
		try 
		{
			FileWriter fw = new FileWriter(fileName);
			if(fw!=null)
			{
				fw.write("");
				fw.flush();
				fw.close();
				}
		}
		catch(Exception e)
		{
			Log.i(TAG,"Error writing to:"+fileName);
		}
	}


	
	/**Appends the input text at the end of the file
	 * @param fileName : to name of the file to which the text shall be appended
	 * @param text : the text to append at the end of the file 
	 */
	static void writeAtEndOfFile(String fileName, String text)
	{
		try 
		{
			FileWriter fw = new FileWriter(fileName, true);
			fw.append(text);
			fw.flush();
			fw.close();
		}
		catch(Exception e)
		{
			Log.i(TAG,"Error writing to:"+fileName);
		}
	}


	/**Replaces the content of the file with the input text 
	 * @param fileName : to name of the file 
	 * @param text : the text to replace the current content of the file 
	 */
	static void writeToFile(String fileName, String text)
	{
		try 
		{
			FileWriter fw = new FileWriter(fileName);
			fw.write(text);
			fw.flush();
			fw.close();
		}
		catch(Exception e)
		{
			Log.i(TAG,"Error writing to:"+fileName);
		}
	}


	/**Replaces the content of the file with the content of the ArrayList<String> in input
	 * @param fileName : to name of the file 
	 * @param stringArray : the array of String objects to replace the current content of the file 
	 */
	static void writeToFile(String fileName, ArrayList<String> stringArray)
	{
		try 
		{
			FileWriter fw = new FileWriter(fileName, true);
			for(int ia=0; ia<stringArray.size(); ia++)
			{
				fw.append(stringArray.get(ia).toString() + System.getProperty("line.separator"));
			}
			fw.flush();
			fw.close();
		}
		catch(Exception e)
		{
			Log.i(TAG,"Error writing to:"+fileName);
		}
	}
}



