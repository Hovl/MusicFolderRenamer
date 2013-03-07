package com.beaglebuddy.mp3.sample_code;

import java.io.IOException;
import java.util.List;

import com.beaglebuddy.mp3.MP3;
import com.beaglebuddy.mp3.enums.Encoding;
import com.beaglebuddy.mp3.enums.FrameType;
import com.beaglebuddy.mp3.id3v23.ID3v23Frame;
import com.beaglebuddy.mp3.id3v23.ID3v23Tag;
import com.beaglebuddy.mp3.id3v23.frame_body.ID3v23FrameBodyTextInformation;
import com.beaglebuddy.mp3.id3v23.frame_body.ID3v23FrameBodyRelativeVolumeAdjustment;


/**
 * Since the MP3 class only provides access methods for the most commonly used .mp3 information, if you wish to access some of the less common information stored in .mp3 files,
 * you will need to do so using the ID3 v2.3 tag directly, as shown below.
 */
public class WorkingWithFrames
{
   /**
    * shows how to directly access the underlying frames found in an ID3v2.3 tag inside an .mp3 file.
    * @param args  command line arguments.
    */
   public static void main(String[] args)
   {
      try
      {
         MP3 mp3 = new MP3("c:/music/ac dc/hells bells.mp3");

         // if there was any invalid information (ie, frames) in the .mp3 file,
         // then display the errors to the user
         if (mp3.hasErrors())
         {
            mp3.displayErrors(System.out);      // display the errors that were found
            mp3.save();                         // discard the invalid information (frames) and
         }                                      // save only the valid frames back to the .mp3 file

         // get the list of frames directly from ID3 v2.3 tag
         ID3v23Tag         id3v23Tag = mp3.getID3v23Tag();
         List<ID3v23Frame> frames    = id3v23Tag.getFrames();


         // remove any existing date frame
         mp3.removeFrame(FrameType.DATE);
         // set the date the song was recorded as July 25th
         ID3v23FrameBodyTextInformation frameBody1 = new ID3v23FrameBodyTextInformation(FrameType.DATE, Encoding.ISO_8859_1, "0725");
         ID3v23Frame                    textFrame  = new ID3v23Frame(FrameType.DATE, frameBody1);
         frames.add(textFrame);


         // remove any existing relative volume frame
         mp3.removeFrame(FrameType.RELATIVE_VOLUME_ADJUSTMENT);
         // set the relative volume adjustments and peak volumes for all 6 channels
         ID3v23FrameBodyRelativeVolumeAdjustment frameBody2 = new ID3v23FrameBodyRelativeVolumeAdjustment(50, 50, 50, 50, 50, 50, 500, 500, 500, 500, 500, 500);
         ID3v23Frame                             volFrame   = new ID3v23Frame(FrameType.RELATIVE_VOLUME_ADJUSTMENT, frameBody2);
         frames.add(volFrame);

         mp3.save();
         System.out.println(mp3);
      }
      catch (IOException ex)
      {
         System.out.println("An error occurred while reading/saving the mp3 file.");
         ex.printStackTrace();
      }
   }
}
