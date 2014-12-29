package com.beaglebuddy.mp3.sample_code;

import java.io.IOException;
import java.util.List;

import com.beaglebuddy.mp3.MP3;
import com.beaglebuddy.mp3.enums.FrameType;
import com.beaglebuddy.mp3.pojo.Level;
import com.beaglebuddy.mp3.id3v23.ID3v23Frame;
import com.beaglebuddy.mp3.id3v23.ID3v23Tag;
import com.beaglebuddy.mp3.id3v23.frame_body.ID3v23FrameBodyEqualization;


/**
 * Since the MP3 class only provides access methods for the most commonly used .mp3 information, it does not provide any methods for setting the eq.
 * Therefore, if you want to set the eq for a song, you will need to do so using the ID3 v2.3 tag directly, as shown below.
 */
public class Equalization
{
   /**
    * shows how to use the Beaglebuddy MP3 library to specify the equalizer settings for an .mp3 file.
    * @param args  command line arguments.
    */
   public static void main(String[] args)
   {
      try
      {
         // set the eq curve on a song
         MP3               mp3       = new MP3("c:\\music\\bon jovi\\livin on a prayer.mp3");
         ID3v23Tag         iD3v23Tag = mp3.getID3v23Tag();
         List<ID3v23Frame> frames    = iD3v23Tag.getFrames();

         // if there was any invalid information (ie, frames) in the .mp3 file, then display the errors to the user
         if (mp3.hasErrors())
         {
            mp3.displayErrors(System.out);      // display the errors that were found
            mp3.save();                         // discard the invalid information (frames) and
         }                                      // save only the valid frames back to the .mp3 file
                                                //
         // create a 32 band eq
         Level[] levels = {new Level(Level.Direction.INCREMENT,       10, 5),
                           new Level(Level.Direction.INCREMENT,       12, 5),
                           new Level(Level.Direction.INCREMENT,       16, 4),
                           new Level(Level.Direction.INCREMENT,       20, 4),
                           new Level(Level.Direction.INCREMENT,       32, 3),
                           new Level(Level.Direction.INCREMENT,       40, 2),
                           new Level(Level.Direction.INCREMENT,       50, 2),
                           new Level(Level.Direction.INCREMENT,       63, 2),
                           new Level(Level.Direction.INCREMENT,       80, 1),
                           new Level(Level.Direction.INCREMENT,      100, 0),
                           new Level(Level.Direction.INCREMENT,      125, 0),
                           new Level(Level.Direction.INCREMENT,      160, 1),
                           new Level(Level.Direction.DECREMENT,      200, 2),
                           new Level(Level.Direction.DECREMENT,      250, 3),
                           new Level(Level.Direction.DECREMENT,      315, 4),
                           new Level(Level.Direction.DECREMENT,      400, 5),
                           new Level(Level.Direction.DECREMENT,      500, 5),
                           new Level(Level.Direction.DECREMENT,      630, 5),
                           new Level(Level.Direction.DECREMENT,      800, 3),
                           new Level(Level.Direction.DECREMENT,     1000, 2),
                           new Level(Level.Direction.DECREMENT,     1250, 1),
                           new Level(Level.Direction.DECREMENT,     1600, 0),
                           new Level(Level.Direction.DECREMENT,     2000, 0),
                           new Level(Level.Direction.INCREMENT,     2500, 2),
                           new Level(Level.Direction.INCREMENT,     3150, 2),
                           new Level(Level.Direction.INCREMENT,     4000, 3),
                           new Level(Level.Direction.INCREMENT,     5000, 3),
                           new Level(Level.Direction.INCREMENT,     6300, 4),
                           new Level(Level.Direction.INCREMENT,     8000, 4),
                           new Level(Level.Direction.INCREMENT,    10000, 5),
                           new Level(Level.Direction.INCREMENT,    12500, 5),
                           new Level(Level.Direction.INCREMENT,    16000, 5)};

         // remove any existing equalization frame
         mp3.removeFrame(FrameType.EQUALIZATION);
         // add the equalization frame to the ID3v2.3 tag
         ID3v23Frame frame = new ID3v23Frame(FrameType.EQUALIZATION, new ID3v23FrameBodyEqualization(5, levels));
         frames.add(frame);
         // save the ID3v2.3 tag to the .mp3 file
         mp3.save();
         System.out.println(mp3);
      }
      catch (IOException ex)
      {
         // an error occurred reading/saving the .mp3 file.
         // you may try to read it again to see if the error still occurs.
         ex.printStackTrace();
      }
   }
}
