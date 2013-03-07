package com.beaglebuddy.mp3.sample_code;

import java.io.File;
import java.io.IOException;

import com.beaglebuddy.mp3.MP3;



/**
 * <p>
 * This class removes invalid information (frames) from your .mp3 files.
 * When the beaglebuddy MP3 library reads in an .mp3 file, it parses and validates all the information stored in the frames in the ID3 tag.
 * If any invalid frames are found, they are removed, and your are left with only the valid ID3 frames.
 * You can then save the mp3 file, and the invalid frames will be discarded.
 * </p>
 * <p>
 * This class recurses through your .mp3 files and cleans them up, removing all the invalid frames.  Examples of invalid frames include comment frames with an empty comment,
 * frames with invalid ID3v2.3 frame ids, Year released frames with no year specified, album picture frames without a picture, etc.
 * </p>
 * <p>
 * The end result of running this class on your .mp3 files is that all of the invalid frames are removed and you have valid ID3v2.3 tags with valid information in your .mp3 files.
 * </p>
 */
public class CleanMP3Files
{
   /**
    * shows how to use the Beaglebuddy MP3 library to make sure all of your .mp3 files only contain valid ID3 information.
	* @param args  command line arguments.
    */
   public static void main(String[] args)
   {
      // start in the root directory
      cleanMp3Songs(new File("c:\\mp3s"));
   }

   /**
    * remove invalid information (frames) from all the .mp3 files in the specified directory.
    * @param directory   directory containing .mp3 files.
    */
   public static void cleanMp3Songs(File directory)
   {
      for(File file : directory.listFiles())
      {
         // if this is a sub-directory, then go examine .mp3 files in it
         if (file.isDirectory())
         {
            cleanMp3Songs(file);
         }
         else if (file.getName().endsWith(".mp3"))
         {
            MP3 mp3 = null;
            try
            {
               mp3 = new MP3(file);
               if (mp3.hasErrors())
               {
                  mp3.displayErrors(System.out);      // display the errors that were found
                  mp3.save();                         // save the valid frames back to the .mp3 file, discarding the invalid frames
               }
               else
               {
                  System.out.println(mp3.getPath());
               }
            }
            catch (IOException ex)
            {
               // an error occurred reading/saving the .mp3 file.
               // you may try to read it again to see if the error still occurs.
               System.err.println("Error occurred while reading/saving ID3 tags from " + file.getPath() + ".");
               if (mp3 != null)
                   System.out.println(mp3);
               ex.printStackTrace();
            }
         }
      }
   }
}
