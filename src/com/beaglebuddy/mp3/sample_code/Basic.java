package com.beaglebuddy.mp3.sample_code;

import java.io.File;
import java.io.IOException;

import com.beaglebuddy.mp3.MP3;
import com.beaglebuddy.mp3.enums.PictureType;


/**
 * This code demonstrates how to open an .mp3 file from a local file system and how to display the information found in the .mp3 file's ID3 tag.
 */
public class Basic
{
   /**
    * shows how to use the Beaglebuddy MP3 library to open an .mp3 file on the local file system.
    * @param args  command line arguments.
    */
   public static void main(String[] args)
   {
      try
      {
         MP3 mp3 = new MP3("C:/temp/01.Hells Bells.mp3");

         // if there was any invalid information (ie, frames) in the .mp3 file,
         // then display the errors to the user
         if (mp3.hasErrors())
         {
            mp3.displayErrors(System.out);      // display the errors that were found
            mp3.save();                         // discard the invalid information (frames) and
         }                                      // save only the valid frames back to the .mp3 file

         // print out all the information about the ID3 v2.3 tag (which holds the information about the song) read from the .mp3 file
         System.out.println(mp3);

         // get some specific information about the song
         System.out.println("audio duration: " + mp3.getAudioDuration()                  + "ms\n"      +
                            "audio size....: " + mp3.getAudioSize()                      + " bytes\n"  +
                            "lyrics by.....: " + mp3.getLyricsBy()                       + "\n"        +
                            "music by......: " + mp3.getMusicBy()                        + "\n"        +
                            "picture.......: " + mp3.getPicture(PictureType.FRONT_COVER) + "\n"        +
                            "publisher.....: " + mp3.getPublisher()                      + "\n"        +
                            "rating........: " + mp3.getRating()                         + "\n"        +
                            "title.........: " + mp3.getTitle()                          + "\n"        +
                            "band..........: " + mp3.getBand()                           + "\n"        +
                            "lyrics........: " + mp3.getLyrics()                         + "\n");

         // store some information about the song in the .mp3 file
         String lyrics = "From the distance came the thunder\n"                          +
                         "And took their breath away\n"                                  +
                         "Eternal lights forever\n"                                      +
                         "The price they had to pay\n"                                   +
                         "\n"                                                            +
                         "The storm of death is comin'\n"                                +
                         "Through the wind and rain\n"                                   +
                         "The midnight bell is tolling\n"                                +
                         "The mob is on it's way\n"                                      +
                         "\n"                                                            +
                         "No one dared to stand in their way\n"                          +
                         "Or else they had to pay\n"                                     +
                         "They don't know They don't know\n"                             +
                         "They're the legions of hell\n"                                 +
                         "They don't know Where to go\n"                                 +
                         "When they're casting the spell\n"                              +
                         "\n"                                                            +
                         "Echoes in the darkness\n"                                      +
                         "Which are hiding from the light\n"                             +
                         "Wheels of time keep turning\n"                                 +
                         "The moon ain't shining bright\n"                               +
                         "\n"                                                            +
                         "Black sun on the horizon\n"                                    +
                         "The air turned to dust\n"                                      +
                         "Rules made from evil\n"                                        +
                         "And the water turned to blood\n"                               +
                         "\n"                                                            +
                         "No one dared to stand in their way\n"                          +
                         "Or else they had to pay\n"                                     +
                         "They don't know They don't know\n"                             +
                         "They're the legions of hell They don't know\n"                 +
                         "Where to go When they're casting the spell\n"                  +
                         "\n"                                                            +
                         "They don't know They don't know\n"                             +
                         "They're the legions of hell\n"                                 +
                         "They don't know Where to go\n"                                 +
                         "When they're casting the spell\n"                              +
                         "\n"                                                            +
                         "The forces of hell, Infernal lights, demonic nights\n"         +
                         "They don't know They don't know They're the legions of hell\n" +
                         "\n"                                                            +
                         "When they're casting the spell\n"                              +
                         "They don't know They don't know\n"                             +
                         "They're the legions of hell\n"                                 +
                         "They don't know Where to go\n"                                 +
                         "When they're casting the spell\n"                              +
                         "\n"                                                            +
                         "We are the legion, the legions of hell\n"                      +
                         "When they're casting the spell I wish you well\n"              +
                         "From the distance from far away they had to pay\n"             +
                         "I wish you well\n"                                             +
                         "\n"                                                            +
                         "That's why they are the legions of hell\n";

         mp3.setBand("Axel Rudi Pell");
         mp3.setAlbum("Kings and Queens");
         mp3.setTitle("Legions Of Hell");
         mp3.setTrack(6);
         mp3.setYear(2004);
         mp3.setLyrics(lyrics);
         mp3.setPicture(PictureType.FRONT_COVER, new File("C:/img/axel_rudi_pell.kings_and_queens.jpg"));

         // save the new information to the .mp3 file
         mp3.save();

         // print out information about the ID3 v2.3 tag (which holds the information about the song) read from the .mp3 file
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
