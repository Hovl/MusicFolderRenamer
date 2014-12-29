package com.beaglebuddy.mp3;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Vector;

import com.beaglebuddy.mp3.enums.Encoding;
import com.beaglebuddy.mp3.enums.FrameType;
import com.beaglebuddy.mp3.enums.Genre;
import com.beaglebuddy.mp3.enums.Language;
import com.beaglebuddy.mp3.enums.PictureType;
import com.beaglebuddy.mp3.exception.TagNotFoundException;
import com.beaglebuddy.mp3.pojo.AttachedPicture;
import com.beaglebuddy.mp3.pojo.SynchronizedLyric;
import com.beaglebuddy.mp3.id3v1.ID3v1Tag;
import com.beaglebuddy.mp3.id3v23.ID3v23Frame;
import com.beaglebuddy.mp3.id3v23.ID3v23Tag;
import com.beaglebuddy.mp3.id3v23.frame_body.ID3v23FrameBodyAttachedPicture;
import com.beaglebuddy.mp3.id3v23.frame_body.ID3v23FrameBodyComments;
import com.beaglebuddy.mp3.id3v23.frame_body.ID3v23FrameBodyPopularimeter;
import com.beaglebuddy.mp3.id3v23.frame_body.ID3v23FrameBodySynchronizedLyricsText;
import com.beaglebuddy.mp3.id3v23.frame_body.ID3v23FrameBodyTextInformation;
import com.beaglebuddy.mp3.id3v23.frame_body.ID3v23FrameBodyUnsynchronizedLyrics;


/**
 * <table class="logos_width">
 *    <tbody>
 *       <tr>
 *          <td                               ><img src="../../../resources/id3v2.gif"                     width="56"  height="54"  alt="ID3 logo"        /></td>
 *          <td class="logos_horz_align_right"><img src="../../../resources/beaglebuddy_software_logo.gif" width="340" height="110" alt="Beaglebuddy logo"/></td>
 *       </tr>
 *    </tbody>
 * </table>
 * <p class="beaglebuddy">
 * This base class provides some underlying methods that help make the derived MP3 class's interface much cleaner and easier to understand.
 * Thus, this class provides lower level methods that are used by the MP3 class, but would otherwise clutter the MP3 class's public interface.
 * </p>
 */
public class MP3Base
{
   // data members
                                      /** if the mp3 file is loaded from a local file, then this data member will contain the path to the .mp3 file. Otherwise, this data member will be null. */
   protected File      mp3File;       /** if the mp3 file is loaded from a URL,        then this data member will contain the URL to the .mp3 file.  Otherwise, this data member will be null. */
   protected URL       mp3Url;        /** ID3v2.3 tag which holds all the information about the .mp3 file.                                                                                     */
   protected ID3v23Tag id3v23Tag;     /** size (in bytes) of the .mp3 file.                                                                                                                    */
   protected long      fileSize;      /** size (in bytes) of the ID3v2.3 tag.                                                                                                                  */
   protected long      tagSize;       /** size (in bytes) of the audio portion of the .mp3 file.  That is, the number of bytes comprising the actual sound data for the song.                  */
   protected long      audioSize;



   /**
    * constructor.
    * @param mp3File   path to the .mp3 file from which to get information.
    * @throws IOException                   if there is a problem reading the .mp3 file.
    */
   public MP3Base(String mp3File) throws IOException
   {
      this(new File(mp3File));
   }

   /**
    * constructor.
    * @param mp3File   .mp3 file from which to get information.
    * @throws IOException   if there is a problem reading the .mp3 file.
    */
   public MP3Base(File mp3File) throws IOException
   {
      this.mp3File = mp3File;
      readID3Tag(new FileInputStream(mp3File));
      this.fileSize  = mp3File.length();
      this.audioSize = fileSize - tagSize;
   }

   /**
    * constructor.
    * @param mp3Url   URL of an .mp3 file from which to get information.
    * @throws IOException   if there is a problem reading the .mp3 file.
    */
   public MP3Base(URL mp3Url) throws IOException
   {
      this.mp3Url = mp3Url;
      URLConnection conn = this.mp3Url.openConnection();
      conn.connect();
      InputStream reader  = mp3Url.openStream();
      readID3Tag(reader);
      this.fileSize  = conn.getContentLength();
      this.audioSize = fileSize - tagSize;
   }

   /**
    * reads in the ID3v2.3 tag from the .mp3 file.  If the .mp3 file does not have an ID3v2.3 tag, then an ID3v1 tag is searched for, and if found, values from it
    * are used to create an ID3v2.3 tag.
    * @param inputStream   input stream pointing to the beginning of an .mp3 file
    * @throws IOException  if there is a problem reading the .mp3 file.
    */
   public void readID3Tag(InputStream inputStream) throws IOException
   {
      try
      {
         this.id3v23Tag = new ID3v23Tag(inputStream);
         this.tagSize   = id3v23Tag.getSize();
      }
      catch (TagNotFoundException ex)
      {
         // the .mp3 file does not contain an ID3v2.3 tag.  create one.
//       System.out.println(mp3File.getPath() + ": " + ex.getMessage());
         this.id3v23Tag = new ID3v23Tag();
         this.tagSize   = 0;
         this.id3v23Tag.setPadding(0);

         // if the .mp3 file is being read from a local file (on the user's hard drive for example), see if it has an ID3v1 tag
         // since the ID3v1 tag is found at the very end of an .mp3 file, you don't want to download the entire .mp3 file just to
         // read the ID3v1 tag if .mp3 file is being read from a URL.
         if (mp3File != null)
         {
            try
            {
               // skip to the ID3v1 tag at the end of the file
               long tagOffset = mp3File.length() - ID3v1Tag.ID3v1_TAG_SIZE;
               if (inputStream.skip(tagOffset) == tagOffset)
               {
                  ID3v1Tag id3v1Tag = new ID3v1Tag(inputStream, getPath());
                  if (id3v1Tag.getAlbum().length()  != 0)
                     setText(id3v1Tag.getAlbum()                     , FrameType.ALBUM_TITLE);
                  if (id3v1Tag.getArtist().length() != 0)
                     setText(id3v1Tag.getArtist()                    , FrameType.BAND);
                  if (id3v1Tag.getTitle().length()  != 0)
                     setText(id3v1Tag.getTitle()                     , FrameType.SONG_TITLE);
                  if (id3v1Tag.getTrack() != 0)
                     setText(id3v1Tag.getTrack()                     , FrameType.TRACK_NUMBER);
                  if (id3v1Tag.getGenreAsString().length() != 0)
                     setText("(" + (id3v1Tag.getGenre() & 0xFF )+ ")", FrameType.CONTENT_TYPE);  // java treats byte as a signed value, while the ID3v2.3 spec treats bytes as unsigned.
                                                                                                 // this necessitates converting a byte to a larger value (integer)
                  if (id3v1Tag.getYear().length() == 4)
                     setText(id3v1Tag.getYear()                      , FrameType.YEAR);
                  save();
               }
            }
            catch (Exception e)
            {
               // nothing to do.  hey, at least we tried.
            }
         }
      }
      finally
      {
         if (inputStream != null)
            try {inputStream.close();} catch (Exception ioex) { /* nothing can be done */ }
      }
   }

   /**
    * determines if the mp3 file had any errors in the information stored in the frames of the ID3v2.3 tag when it was read in.  If any of the frames were invalid, then they are
    * removed from the ID3v2.3 tag.
    * @return whether any invalid frames were encountered while reading in the ID3v2.3 tag from the .mp3 file.
    */
   public boolean hasErrors()
   {
      return id3v23Tag.getInvalidFrames().size() != 0;
   }

   /**
    * gets a list of any errors encountered while reading in the ID3v2.3 tag.
    * @return a list of errors that occurred while reading in the ID3v2.3 tag.
    */
   public List<String> getErrors()
   {
      Vector<String>    errors        = new Vector<String>();
      List<ID3v23Frame> invalidFrames = id3v23Tag.getInvalidFrames();

      for(ID3v23Frame frame : invalidFrames)
         errors.add(frame.getInvalidMessage());

      return errors;
   }

   /**
    * displays any errors found while reading in the ID3v2.3 tag from the .mp3 file.  If no errors were found, then nothing is written to the print stream.
    * @param printStream   print stream used to write out the errors.
    */
   public void displayErrors(PrintStream printStream)
   {
      List<String> errors = getErrors();

      if (errors.size() != 0)
      {
         printStream.println(getPath() + " had " + errors.size() + " invalid frames");
         for(String error : errors)
            printStream.println("   " + error);
      }
   }

   /**
    * gets the path to the .mp3 file.
    * <br/><br/><b>Example:</b><code><pre>
    *     // load the song "Take Hold of the Flame" from Queensryche's album, "The Warning".
    *     MP3 mp3 = new MP3("c:/mp3/queensryche/the warning/take hold of the flame.mp3");
    *     System.out.println("the mp3 file was loaded from " + mp3.getPath());
    *
    *     // load the song "Take Hold of the Flame" from Queensryche's album, "The Warning".
    *     MP3 mp3 = new MP3("http://www.beaglebuddy.com/mp3/queensryche/the warning/take hold of the flame.mp3");
    *     System.out.println("the mp3 file was loaded from " + mp3.getPath());</pre></code>
    * @return the path to the .mp3 file.
    */
   public String getPath()
   {
      return mp3File == null ? mp3Url.toExternalForm() : mp3File.getPath();
      // mp3Url.getProtocol() + "//" + mp3Url.getHost() + ":" + mp3Url.getPort() + mp3Url.getFile()
   }

   /**
    * gets the ID3 v2.3 Tag.
    * @return the ID3 v2.3 Tag.
    */
   public ID3v23Tag getID3v23Tag()
   {
      return id3v23Tag;
   }

   /**
    * add a frame of the specified type to the ID3v2.3 tag.
    * @param frameType   ID3v2.3 frame type.
    * @return the new frame.
    */
   public ID3v23Frame addFrame(FrameType frameType)
   {
      ID3v23Frame frame = null;
      frame = new ID3v23Frame(frameType);
      id3v23Tag.getFrames().add(frame);

      return frame;
   }

   /**
    * get the first frame with the specified frame id.
    * @param frameType   ID3v2.3 frame type.
    * @return the first frame found with the given frame Id, or null if no frame with the specified id can be found.
    */
   protected ID3v23Frame getFrame(FrameType frameType)
   {
      for(ID3v23Frame frame : id3v23Tag.getFrames())
         if (frame.getHeader().getFrameType().equals(frameType))
            return frame;

      return null;
   }

   /**
    * get all the frames with the specified frame id.
    * @param frameType   ID3v2.3 frame type.
    * @return all the frames found with the given frame Id, or an empty collection of size 0 if no frame with the specified id can be found.
    */
   protected Vector<ID3v23Frame> getFrames(FrameType frameType)
   {
      Vector<ID3v23Frame> frames = new Vector<ID3v23Frame>();

      for(ID3v23Frame frame : id3v23Tag.getFrames())
         if (frame.getHeader().getFrameType().equals(frameType))
            frames.add(frame);

      return frames;
   }

   /**
    * removes the first frame with the specified frame id.
    * @param frameType   ID3v2.3 frame type.
    * @return the first frame found with the given frame Id, or null if no frame with the specified id can be found.
    */
   public ID3v23Frame removeFrame(FrameType frameType)
   {
      ID3v23Frame found = null;
      for(ID3v23Frame frame : id3v23Tag.getFrames())
      {
         if (frame.getHeader().getFrameType().equals(frameType))
         {
            found = frame;
            break;
         }
      }
      if (found != null)
        id3v23Tag.getFrames().remove(found);

      return found;
   }

   /**
    * removes all the frames with the specified frame id.
    * @param frameType   ID3v2.3 frame type.
    * @return the first frame found with the given frame Id, or null if no frame with the specified id can be found.
    */
   public Vector<ID3v23Frame> removeFrames(FrameType frameType)
   {
      // get a list of all the frames of type frameId
      Vector<ID3v23Frame> found = new Vector<ID3v23Frame>();
      for(ID3v23Frame frame : id3v23Tag.getFrames())
      {
         if (frame.getHeader().getFrameType().equals(frameType))
            found.add(frame);
      }
      // remove them from the ID3v2.3 tag
      for(ID3v23Frame frame : found)
        id3v23Tag.getFrames().remove(frame);

      return found;
   }

   /**
    * returns the size (in bytes) of the .mp3 file.
    * @return the size (in bytes) of the .mp3 file.
    */
   public long getFileSize()
   {
      return mp3File.length();
   }

   /**
    * gets the ID3 V2.3 frame containing the attached picture of the specified picture type.
    * @param pictureType   One of the 21 allowable ID3v2.3 picture types.
    * @return the ID3 V2.3 frame containing the attached picture of the specified picture type.
    * If no picture with the specified picture type have been specified, then null is returned.
    * @throws IllegalArgumentException   if the picture type is not a valid ID3V2.3 picture type.
    */
   protected ID3v23Frame getAttachedPictureFrame(PictureType pictureType) throws IllegalArgumentException
   {
      ID3v23Frame         found  = null;
      Vector<ID3v23Frame> frames = getFrames(FrameType.ATTACHED_PICTURE);

      for(ID3v23Frame frame : frames)
      {
         ID3v23FrameBodyAttachedPicture frameBody = (ID3v23FrameBodyAttachedPicture)frame.getBody();
         if (frameBody.getPictureType() == pictureType)
            found = frame;
      }
      return found;
   }

   /**
    * sets the attached picture for the specified picture type.
    * @param attachedPicture   attached picture containing the information about the image to be added to the ID3V2.3 tag.
    * @throws IllegalArgumentException   if the picture type is not a valid ID3V2.3 picture type.
    */
   protected void setAttachedPicture(AttachedPicture attachedPicture) throws IllegalArgumentException
   {
      ID3v23Frame                    frame     = getAttachedPictureFrame(attachedPicture.getPictureType());
      ID3v23FrameBodyAttachedPicture frameBody = null;

      if (frame == null)
         frame = addFrame(FrameType.ATTACHED_PICTURE);

      frameBody = (ID3v23FrameBodyAttachedPicture)frame.getBody();
      frameBody.setEncoding   (Encoding.UTF_16);
      frameBody.setMimeType   (attachedPicture.getMimeType());
      frameBody.setPictureType(attachedPicture.getPictureType());
      frameBody.setDescription(attachedPicture.getDescription());
      frameBody.setImage      (attachedPicture.getImage());
   }

   /**
    * removes the attached picture for the specified picture type.
    * @param pictureType  one of the 21 valid ID3v2.3 picture types.
    */
   protected void removeAttachedPicture(PictureType pictureType) throws IllegalArgumentException
   {
      ID3v23Frame frame = getAttachedPictureFrame(pictureType);
      if (frame != null)
         id3v23Tag.getFrames().remove(frame);
   }

   /**
    * gets the comments about the song in the specified language.
    * @param language   the ISO-639-2 language code of the language the comments about the song are written in.
    * @return the comments about the song in the specified language.
    * If no comments about the song in the specified language have been specified, then null is returned.
    * @throws IllegalArgumentException   if the specified language is not a valid ISO-639-2 language code.
    */
   protected String getComments(Language language) throws IllegalArgumentException
   {
      ID3v23Frame frame  = getCommentsFrame(language);
      return frame == null ? null : ((ID3v23FrameBodyComments)frame.getBody()).getText();
   }

   /**
    * gets the ID3 V2.3 frame containing the comments in the specified language to the song.
    * @param language   the ISO-639-2 language code of the language the comments are written in.
    * @return the ID3 V2.3 frame containing the comments in the specified language.
    * If no comments in the specified language have been specified, then null is returned.
    * @throws IllegalArgumentException   if the specified language is not a valid ISO-639-2 language code.
    */
   private ID3v23Frame getCommentsFrame(Language language) throws IllegalArgumentException
   {
      ID3v23Frame         found  = null;
      Vector<ID3v23Frame> frames = getFrames(FrameType.COMMENTS);

      for(ID3v23Frame frame : frames)
      {
         ID3v23FrameBodyComments frameBody = (ID3v23FrameBodyComments)frame.getBody();
         if (frameBody.getLanguage() == language)
            found = frame;
      }
      return found;
   }

   /**
    * sets the comments about the song in the specified language encoded as a UTF-16 string.
    * @param language   the ISO-639-2 language code of the language the comments about the song are written in.
    * @param comments   the comments about the song.
    * @throws IllegalArgumentException   if the specified language is not a valid ISO-639-2 language code or if the comments are empty.
    */
   protected void setComments(Language language, String comments) throws IllegalArgumentException
   {
      if (comments == null || comments.trim().length() == 0)
         throw new IllegalArgumentException("Invalid comments.  They can not be null or empty.");

      ID3v23Frame             frame     = getCommentsFrame(language);
      ID3v23FrameBodyComments frameBody = null;

      if (frame == null)
         frame = addFrame(FrameType.COMMENTS);

      frameBody = (ID3v23FrameBodyComments)frame.getBody();
      frameBody.setEncoding(Encoding.UTF_16);
      frameBody.setLanguage(language);
      frameBody.setText    (comments);
   }

   /**
    * removes the comments to the song in the specified language from the ID3 V2.3 tag.
    * @param language   the ISO-639-2 language code of the language the comments about the song are written in.
    * @throws IllegalArgumentException   if the specified language is not a valid ISO-639-2 language code.
    */
   protected void removeComments(Language language) throws IllegalArgumentException
   {
      ID3v23Frame frame = getCommentsFrame(language);
      if (frame != null)
         id3v23Tag.getFrames().remove(frame);
   }

   /**
    * @return the genre as a string.
    * @param genre   integer value of the ID3v1 genre.
    */
   protected String getGenreAsString(byte genre)
   {
      return Genre.getGenre(genre).getName();
   }

   /**
    * sets the rating of the song.
    * @param rating   the rating of the song where 1 is worst and 255 is best. 0 is unknown.
    * @throws IllegalArgumentException   if the rating is not between 0 and 255, inclusive.  That is, 0 <= rating <= 255.
    */
   public void setPopularimiter(int rating)
   {
      if (rating < ID3v23FrameBodyPopularimeter.UNKNOWN || rating > ID3v23FrameBodyPopularimeter.BEST)
         throw new IllegalArgumentException("Invalid rating, " + rating + ".  It must be between " + ID3v23FrameBodyPopularimeter.UNKNOWN + " and " + ID3v23FrameBodyPopularimeter.BEST + ".");

      ID3v23FrameBodyPopularimeter frameBody = null;
      ID3v23Frame                  frame     = getFrame(FrameType.POPULARIMETER);

      if (frame == null)
         frame = addFrame(FrameType.POPULARIMETER);

      frameBody = (ID3v23FrameBodyPopularimeter)(frame.getBody());
      frameBody.setRating(rating);
   }

   /**
    * gets the synchronized lyrics in the specified language to the song.
    * @param language   the ISO-639-2 language code of the language the lyrics to the song are written in.
    * @return the synchronized lyrics to the song in the specified language.
    * If no synchronized lyrics in the specified language have been specified, then null is returned.
    */
   protected List<SynchronizedLyric> getSynchronizedLyrics(Language language)
   {
      ID3v23Frame frame  = getSynchronizedLyricsFrame(language);
      return frame == null ? null : ((ID3v23FrameBodySynchronizedLyricsText)frame.getBody()).getSynchronizedLyrics();
   }

   /**
    * gets the ID3 V2.3 frame containing the synchronized lyrics in the specified language to the song.
    * @param language   the ISO-639-2 language code of the language the synchronized lyrics to the song are written in.
    * @return the ID3 V2.3 frame containing the synchronized lyrics to the song in the specified language.
    * If no synchronized lyrics in the specified language have been specified, then null is returned.
    */
   private ID3v23Frame getSynchronizedLyricsFrame(Language language)
   {
      ID3v23Frame         found  = null;
      Vector<ID3v23Frame> frames = getFrames(FrameType.SYNCHRONIZED_LYRIC_TEXT);

      for(ID3v23Frame frame : frames)
      {
         ID3v23FrameBodySynchronizedLyricsText frameBody = (ID3v23FrameBodySynchronizedLyricsText)frame.getBody();
         if (frameBody.getLanguage() == language)
            found = frame;
      }
      return found;
   }

   /**
    * sets the synchronized (english) lyrics to the song encoded using the UTF-16  character set.
    * @param synchronizedLyrics   the synchronized (english) lyrics to the song.
    */
   protected void setSynchronizedLyrics(List<SynchronizedLyric> synchronizedLyrics)
   {
      setSynchronizedLyrics(Encoding.UTF_16, Language.ENG, synchronizedLyrics);
   }

   /**
    * sets the synchronized lyrics.
    * @param encoding            the character set used to encode the lyrics.  Only ISO 8859-1 and UTF-16 are allowed.
    * @param language            the ISO-639-2 language code of the language the lyrics to the song are written in.
    * @param synchronizedLyrics  the synchronized lyrics to the song.
    * @throws IllegalArgumentException   if the synchronized lyrics are not sorted in ascending chronological order.
    */
   protected void setSynchronizedLyrics(Encoding encoding, Language language, List<SynchronizedLyric> synchronizedLyrics) throws IllegalArgumentException
   {
      ID3v23Frame                           frame     = getSynchronizedLyricsFrame(language);
      ID3v23FrameBodySynchronizedLyricsText frameBody = null;

      if (frame == null)
         frame = addFrame(FrameType.SYNCHRONIZED_LYRIC_TEXT);

      frameBody = (ID3v23FrameBodySynchronizedLyricsText)frame.getBody();
      frameBody.setEncoding          (encoding);
      frameBody.setLanguage          (language);
      frameBody.setSynchronizedLyrics(synchronizedLyrics);
   }

   /**
    * removes the synchronized lyrics to the song in the specified language from the ID3 V2.3 tag.
    * @param language   the ISO-639-2 language code of the language the lyrics to the song are written in.
    */
   protected void removeSynchronizedLyrics(Language language)
   {
      ID3v23Frame frame = getSynchronizedLyricsFrame(language);
      if (frame != null)
         id3v23Tag.getFrames().remove(frame);
   }

   /**
    * gets the text from a text frame and converts it to an integer.
    * @return the text of a text frame as an integer.  If no number has been specified or if the number is less <= 0, then 0 is returned.
    * @param frameType   ID3v2.3 text frame type.
    *
    */
   public int getTextAsInteger(FrameType frameType)
   {
      ID3v23Frame frame = getFrame(frameType);
      int         n     = 0;

      if (frame != null)
      {
         ID3v23FrameBodyTextInformation frameBody = (ID3v23FrameBodyTextInformation)frame.getBody();
         try
         {
            n = Integer.parseInt(frameBody.getText());
            // if an invalid value has been specified, simply return 0.
            if (n < 0)
               n = 0;
         }
         catch (NumberFormatException ex)
         {
            // if an invalid value has been specified, simply return 0.
         }
      }
      return n;
   }

   /**
    * sets the text in a text frame using UTF-16  encoding.
    * @param text     the text of the frame to set.
    * @param frameType   ID3v2.3 text frame type.
    */
   protected void setText(String text, FrameType frameType)
   {
      setText(Encoding.UTF_16, text, frameType);
   }

   /**
    * sets the text in a text frame using the specified encoding.
    * @param encoding   the character set used to encode the text.  Only ISO 8859-1 and UTF-16 are allowed.
    * @param text       the text of the frame to set.
    * @param frameType  ID3v2.3 text frame type.
    */
   protected void setText(Encoding encoding, String text, FrameType frameType)
   {
      ID3v23FrameBodyTextInformation frameBody = null;
      ID3v23Frame                    frame     = getFrame(frameType);

      if (frame == null)
         frame = addFrame(frameType);

      frameBody = (ID3v23FrameBodyTextInformation)frame.getBody();
      frameBody.setEncoding(encoding);
      frameBody.setText    (text);
   }

   /**
    * sets the text in a text frame where the text is an integer string.
    * @param n          the integer to be converted to text and stored in a text frame.
    * @param frameType  ID3v2.3 text frame type.
    * @throws IllegalArgumentException   If the number n is <= 0.
    */
   public void setText(int n, FrameType frameType) throws IllegalArgumentException
   {
      if (n <= 0)
         throw new IllegalArgumentException("Invalid number specified, " + n + ".  It must be greater than or equal to 1.");

      setText((String.valueOf(n)), frameType);
   }

   /**
    * gets the unsynchronized lyrics in the specified language to the song.
    * @param language   the ISO-639-2 language code of the language the lyrics to the song are written in.
    * @return the lyrics to the song in the specified language.
    * If no lyrics in the specified language have been specified, then null is returned.
    * @throws IllegalArgumentException   if the specified language is not a valid ISO-639-2 language code.
    */
   protected String getUnsynchronizedLyrics(Language language) throws IllegalArgumentException
   {
      ID3v23Frame frame  = getUnsynchronizedLyricsFrame(language);
      return frame == null ? null : ((ID3v23FrameBodyUnsynchronizedLyrics)frame.getBody()).getText();
   }

   /**
    * gets the ID3 V2.3 frame containing the unsynchronized lyrics in the specified language to the song.
    * @param language   the ISO-639-2 language code of the language the lyrics to the song are written in.
    * @return the ID3 V2.3 frame containing the unsynchronized lyrics to the song in the specified language.
    * If no lyrics in the specified language have been specified, then null is returned.
    * @throws IllegalArgumentException   if the specified language is not a valid ISO-639-2 language code.
    */
   private ID3v23Frame getUnsynchronizedLyricsFrame(Language language) throws IllegalArgumentException
   {
      ID3v23Frame         found  = null;
      Vector<ID3v23Frame> frames = getFrames(FrameType.UNSYCHRONIZED_LYRICS);

      for(ID3v23Frame frame : frames)
      {
         ID3v23FrameBodyUnsynchronizedLyrics frameBody = (ID3v23FrameBodyUnsynchronizedLyrics)frame.getBody();
         if (frameBody.getLanguage() == language)
            found = frame;
      }
      return found;
   }

   /**
    * sets the unsynchronized (english) lyrics to the song encoded using the UTF-16 character set.
    * @param lyrics   the lyrics to the song.
    */
   protected void setUnsynchronizedLyrics(String lyrics)
   {
      setUnsynchronizedLyrics(Encoding.UTF_16, Language.ENG, lyrics);
   }

   /**
    * sets the unsynchronized lyrics.
    * @param encoding   the character set used to encode the lyrics.  Only ISO 8859-1 and UTF-16 are allowed.
    * @param language   the ISO-639-2 language code of the language the lyrics to the song are written in.
    * @param lyrics     the lyrics to the song.
    * @throws IllegalArgumentException   if the specified language is not a valid ISO-639-2 language code.
    */
   protected void setUnsynchronizedLyrics(Encoding encoding, Language language, String lyrics) throws IllegalArgumentException
   {
      ID3v23Frame                         frame     = getUnsynchronizedLyricsFrame(language);
      ID3v23FrameBodyUnsynchronizedLyrics frameBody = null;

      if (frame == null)
         frame = addFrame(FrameType.UNSYCHRONIZED_LYRICS);

      frameBody = (ID3v23FrameBodyUnsynchronizedLyrics)frame.getBody();
      frameBody.setEncoding(encoding);
      frameBody.setLanguage(language);
      frameBody.setText    (lyrics);
   }

   /**
    * removes the unsynchronized lyrics to the song in the specified language from the ID3 V2.3 tag.
    * @param language   the ISO-639-2 language code of the language the lyrics to the song are written in.
    * @throws IllegalArgumentException   if the specified language is not a valid ISO-639-2 language code.
    */
   protected void removeUnsynchronizedLyrics(Language language) throws IllegalArgumentException
   {
      ID3v23Frame frame = getUnsynchronizedLyricsFrame(language);
      if (frame != null)
         id3v23Tag.getFrames().remove(frame);
   }

   /**
    * renames the temporary file name to the current .mp3 filename.
    * @param tempFile   temporary .mp3 file which will be renamed to the .mp3 file.
    * @param mp3File    current .mp3 file which will be deleted.
    * @throws IOException if the current .mp3 file can not be deleted or if the temporary .mp3 file can not be renamed to the current .mp3 file.
    */
   private static void rename(File tempFile, File mp3File) throws IOException
   {
      if (!mp3File.delete())
         throw new IOException("Unable to delete the file " + mp3File.getPath());
      if (!tempFile.renameTo(mp3File))
         throw new IOException("Unable to rename the file " + tempFile.getPath() + " to " + mp3File.getPath() + ".");
   }

   /**
    * save the ID3v2.3 tag to the .mp3 file.
    * This is a very messy method, and you really have to understand the ID3v2.3 structure to understand this method.
    * So, if you can, by all means, avoid reading the code in this method.
    * <br/><br/>
    * @throws IOException              if there was an error writing the ID3v2.3 tag to the .mp3 file.
    * @throws IllegalStateException    if the mp3 song was loaded from a URL and is therefore considered to be read only and thus may not be modified.
    */
   public void save() throws IOException, IllegalStateException
   {
      setText((int)audioSize, FrameType.SIZE);                                   // set the size (in bytes) of the audio portion of the .mp3 in a TSIZ frame
      long oldTagSize     = tagSize;                                             // get the size of the tag before any changes were made
      long oldPaddingSize = id3v23Tag.getPadding().length;                       // get the size of the padding in the tag before any changes were made
                            id3v23Tag.setBuffer();                               // save any changes to the tag's internal byte buffer
      long newTagSize     = id3v23Tag.getSize() - oldPaddingSize;                // get the size of the tag after any changes were made

      // if the new tag is smaller than the old tag, then we can just re-use the old tag's space and adjust the padding
      if (newTagSize < oldTagSize)
      {
         id3v23Tag.setPadding((int)(oldTagSize - newTagSize));
         RandomAccessFile file = new RandomAccessFile(mp3File, "rwd");           // open the mp3 file for writing
         id3v23Tag.save(file);                                                   // write the ID3v2.3 tag to the beginning of the .mp3 file
         file.close();
      }
      // otherwise, we need to re-write the whole .m3 file so that we have enough space to accommodate the tag's new larger size.
      else
      {
         File             tempFile     = new File(mp3File.getPath() + ".tmp");   // name of the temporary .mp3 file
         FileOutputStream tempMp3File  = new FileOutputStream(tempFile);         // output stream used to write the bytes to the temp    .mp3 file
         FileInputStream  audioFile    = new FileInputStream (mp3File);          // input  stream used to read  the audio of the current .mp3 file
         byte[]           audio        = new byte[2048];                         // buffer used to read the audio bytes from the current .mp3 file to the temp .mp3 file

         id3v23Tag.setPadding(ID3v23Tag.DEFAULT_PADDING_SIZE);                   // set the padding to a default size
         id3v23Tag.save(tempMp3File);                                            // save the new ID3v2.3 tag to the beginning of the new .mp3 file

         // copy the audio portion of the old .mp3 file to the new one
         int n = 0;
         int audioSizeWritten = 0;
         audioFile.skip(oldTagSize);                                             // skip to the audio portion of the current .mp3 file

         while ((n = audioFile.read(audio)) != -1)
         {
            audioSizeWritten+=n;
            tempMp3File.write(audio, 0, n);
         }
         tempMp3File.close();
         audioFile.close();

         if (audioSizeWritten != audioSize)
             throw new IOException("Error saving the audio portion.  Expected " + audioSize + " bytes, but saved " + audioSizeWritten + " bytes.");

         rename(tempFile, mp3File);
      }
   }

   /**
    * @return string representation of the mp3 file.
    */
   public String toString()
   {
      StringBuffer buffer = new StringBuffer();
      buffer.append("mp3 file.....: " + getPath() + "\n");
      buffer.append("mp3 file size: " + fileSize  + " bytes\n");
      buffer.append("audio size...: " + audioSize + " bytes\n");
      buffer.append("ID3v2.3 tag..: " + id3v23Tag + "\n");

      return buffer.toString();
   }
}
