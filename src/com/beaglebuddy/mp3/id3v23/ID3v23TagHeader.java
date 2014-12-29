package com.beaglebuddy.mp3.id3v23;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.beaglebuddy.mp3.exception.TagNotFoundException;
import com.beaglebuddy.mp3.id3v23.frame_body.ID3v23FrameBodyUtility;





/**
 * <table class="logos_width">
 *    <tbody>
 *       <tr>
 *          <td                               ><img src="../../../../resources/id3v2.gif"                     width="56"  height="54"  alt="ID3 logo"        /></td>
 *          <td class="logos_horz_align_right"><img src="../../../../resources/beaglebuddy_software_logo.gif" width="340" height="110" alt="Beaglebuddy logo"/></td>
 *       </tr>
 *    </tbody>
 * </table>
 * <p class="beaglebuddy">
 * This class provides methods for reading and writing the ID3V2.3 tag header, including the optional extended tag header.
 * The ID3v2 tag header is 10 bytes long and is the first information in the {@link com.beaglebuddy.mp3.id3v23.ID3v23Tag ID3v2.3 tag}.  A tag header has the following fields:
 * <table class="beaglebuddy">
 *    <caption><b>Tag Header Fields</b></caption>
 *    <thead>
 *       <tr><th class="beaglebuddy">&nbsp;</th><th class="beaglebuddy">Field</th><th class="beaglebuddy">Description</th></tr>
 *    </thead>
 *    <tbody>
 *       <tr><td class="beaglebuddy">1. </td><td class="beaglebuddy">unsynchronization    </td><td class="beaglebuddy">used to make the ID3v2.3 tag as compatible as possible with existing software.  See the <a href="http://www.id3.org/id3v2.3.0#sec5">unsynchronization scheme</a>
 *                                                                                                                     section of the ID3v2.3 specification for more details about this field.                                                                                         </td></tr>
 *       <tr><td class="beaglebuddy">2. </td><td class="beaglebuddy">extendedHeaderPresent</td><td class="beaglebuddy">indicates whether an extended header is present.  If so, then the extended header fields <i>extendedHeaderSize</i>, <i>CRCDataPresent</i>,
 *                                                                                                                     <i>paddingSize</i>, and <i>CRCData</i>  are available.                                                                                                          </td></tr>
 *       <tr><td class="beaglebuddy">3. </td><td class="beaglebuddy">experimentalIndicator</td><td class="beaglebuddy">indicates that the tag is in an experimental stage.  I have no idea why this flag is here.                                                                      </td></tr>
 *       <tr><td class="beaglebuddy">4. </td><td class="beaglebuddy">tagSize              </td><td class="beaglebuddy">holds the total size of the ID3v2.3 tag, not including the header, but including the extended header.  Simply put, since the tag header is 10 bytes long, the
 *                                                                                                                     <i>tagSize</i> = total tag size - 10                                                                                                                            </td></tr>
 *       <tr><td class="beaglebuddy">5. </td><td class="beaglebuddy">extendedHeaderSize   </td><td class="beaglebuddy">valid only if the <i>extendedHeaderPresent</i> flag is set, this field contains the total size of the extended header, which is either 6 or 10 bytes.           </td></tr>
 *       <tr><td class="beaglebuddy">6. </td><td class="beaglebuddy">CRCDataPresent       </td><td class="beaglebuddy">valid only if the <i>extendedHeaderPresent</i> flag is set, this flag indicates whether the CRC for the .mp3 file has been calculated and the result stored in
 *                                                                                                                     in the <i>CRCData</i> field.                                                                                                                                    </td></tr>
 *       <tr><td class="beaglebuddy">7. </td><td class="beaglebuddy">paddingSize          </td><td class="beaglebuddy">valid only if the <i>extendedHeaderPresent</i> flag is set, this field contains the number of bytes of padding stored after the frames.                         </td></tr>
 *       <tr><td class="beaglebuddy">8. </td><td class="beaglebuddy">CRCData              </td><td class="beaglebuddy">valid only if the <i>extendedHeaderPresent</i> and <i>CRCDataPresent</i> flags are set, this field contains the actual CRC data for the .mp3 file.              </td></tr>
 *    </tbody>
 * </table>
 * </p>
 * @see <a href="http://www.id3.org/id3v2.3.0/"    target="_blank">ID3 tag version 2.3.0 standard</a>
 * @see <a href="http://en.wikipedia.org/wiki/ID3" target="_blank">wikipedia history of ID3 tags</a>
 */
public class ID3v23TagHeader
{
   // class members
                                                                                      /** size (in bytes) of the standard ID3v2.3 tag header */
   public  static final int  ID3V2_3_TAG_HEADER_STANDARD_SIZE                 = 10;
   private static final int  ID3V2_3_TAG_HEADER_EXTENDED_HEADER_DEFAULT_SIZE  = 10;
   private static final int  ID3v2_3_TAG_HEADER_EXTENDED_HEADER_CRC_DATA_SIZE = 4;

   private static final byte ID3v2_3_TAG_HEADER_UNSYNCHRONIZATION_MASK       = (byte)0x80;
   private static final byte ID3v2_3_TAG_HEADER_EXTENDED_HEADER_PRESENT_MASK = (byte)0x40;
   private static final byte ID3v2_3_TAG_HEADER_EXPERIMENTAL_INDICATOR_MASK  = (byte)0x20;
   private static final byte ID3v2_3_TAG_HEADER_EXTENDED_HEADER_CRC_MASK     = (byte)0x80;


   // data members
   private byte[]  header;                   // the raw binary data of the tag header.
   private boolean dirty;                    // whether the tag header has been modified

   // standard header fields
   private boolean unsynchronization;        // whether or not unsynchronization is used.
   private boolean extendedHeaderPresent;    // whether or not the tag header is followed by an extended tag header
   private boolean experimentalIndicator;    // whether the tag is in an experimental stage.
   private int     tagSize;                  // the total size of the ID3v2.3 tag excluding the tag header

   // extended header fields
   private int     extendedHeaderSize;       // the size (6 or 10 bytes) of the extended header (not including itself) depending on whether CRC data is present.
   private boolean CRCDataPresent;           // whether CRC data is present.
   private int     paddingSize;              // the size of the padding after the last frame and before the start of the audio data.
   private byte[]  CRCData;                  // raw binary data containing the CRC.





   /**
    * The default constructor is called when creating a new tag header.
    * The default values used are:
    * <ul>
    *    <li>tag size is 0 bytes            </li>
    *    <li>no unsynchronization           </li>
    *    <li>no extended header present     </li>
    *    <li>experimental indicator is false</li>
    *    <li>
    * </ul>
    */
   public ID3v23TagHeader()
   {
      header = new byte[ID3V2_3_TAG_HEADER_STANDARD_SIZE];

      header[0] = 0x49;  // I
      header[1] = 0x44;  // D
      header[2] = 0x33;  // 3
      header[3] = 0x03;  // version 2.3.0
      header[4] = 0x00;  // version 2.3.0
      header[5] = 0x00;  // flags
      header[6] = 0x00;  // tag size = 0
      header[7] = 0x00;
      header[8] = 0x00;
      header[9] = 0x00;
      CRCData   = new byte[0];
      dirty     = true;  // the tag header has been created, but has not yet been saved
   }

   /**
    * This constructor is called when reading in an existing tag header from an .mp3 file.
    * @param inputStream  input stream pointing to the tag header in the .mp3 file.
    * @throws IOException            if the tag header can not be loaded from the .mp3 file.
    * @throws TagNotFoundException   if the .mp3 file does not contain an ID3v2.3 tag header.
    */
   public ID3v23TagHeader(InputStream inputStream) throws IOException, TagNotFoundException
   {
      byte[] buffer1 = new byte[ID3V2_3_TAG_HEADER_STANDARD_SIZE];
      byte[] buffer2 = new byte[0];
             CRCData = new byte[0];

      // see if the mp3 file contains an ID3v2.3 tag
      if (inputStream.read(buffer1) == buffer1.length                                   &&  // read in the default tag header
          (char)buffer1[0] == 'I' && (char)buffer1[1] == 'D' && (char)buffer1[2] == '3' &&  // see if it starts with ID3
          buffer1[3] == 0x03 && buffer1[4] == 0x00)                                         // see if its version 2.3.0
      {
         unsynchronization     = (buffer1[5] & ID3v2_3_TAG_HEADER_UNSYNCHRONIZATION_MASK      ) != 0;
         extendedHeaderPresent = (buffer1[5] & ID3v2_3_TAG_HEADER_EXTENDED_HEADER_PRESENT_MASK) != 0;
         experimentalIndicator = (buffer1[5] & ID3v2_3_TAG_HEADER_EXPERIMENTAL_INDICATOR_MASK ) != 0;
         tagSize               = (buffer1[6] << 21) + (buffer1[7] << 14) + (buffer1[8] << 7) + buffer1[9];

         // if an extended header is present, read it in
         if (extendedHeaderPresent)
         {
            buffer2 = new byte[ID3V2_3_TAG_HEADER_EXTENDED_HEADER_DEFAULT_SIZE];

            if (inputStream.read(buffer2) != ID3V2_3_TAG_HEADER_EXTENDED_HEADER_DEFAULT_SIZE)
               throw new IOException("Unable to read the ID3v2.3 extended tag header.");

            extendedHeaderSize = (buffer2[0] << 24) + (buffer2[1] << 16) + (buffer2[2] << 8) + buffer2[3];
            CRCDataPresent     = (buffer2[4] & ID3v2_3_TAG_HEADER_EXTENDED_HEADER_CRC_MASK) != 0;
            paddingSize        = (buffer2[6] << 24) + (buffer2[7] << 16) + (buffer2[8] << 8) + buffer2[9];

            // if CRC data is present, read it in
            if (CRCDataPresent)
            {
               CRCData = new byte[ID3v2_3_TAG_HEADER_EXTENDED_HEADER_CRC_DATA_SIZE];
               if (inputStream.read(CRCData) != ID3V2_3_TAG_HEADER_EXTENDED_HEADER_DEFAULT_SIZE)
                  throw new IOException("Unable to read the ID3v2.3 CRC data from the extended tag header.");
            }
         }
         int tagHeaderSize = buffer1.length + buffer2.length + CRCData.length;
         int index         = 0;
         header = new byte[tagHeaderSize];
         System.arraycopy(buffer1, 0, header, 0, buffer1.length);
         index = buffer1.length;
         System.arraycopy(buffer2, 0, header, index, buffer2.length);
         index += buffer2.length;
         System.arraycopy(CRCData, 0, header, index, CRCData.length);
         dirty = false;
      }
      else
      {
         throw new TagNotFoundException("An ID3v2.3 tag was not found in the .mp3 file.");
      }
   }

   /**
    * indicates whether or not the tag header's fields have been modified.
    * @return whether or not the tag header has been modified.
    * @see #setBuffer()
    */
   public boolean isDirty()
   {
      return dirty;
   }

   /**
    * I have no idea what this meas.  Seriously.
    * @return whether or not unsynchronization is used.
    * @see #setUnsynchronization(boolean)
    * @see <a href="http://www.id3.org/id3v2.3.0#sec5">ID3v2.3 unsynchronization scheme</a>
    */
   public boolean isUnsynchronization()
   {
      return unsynchronization;
   }

   /**
    * sets whether unsynchronization is used.
    * @param unsynchronization    boolean indicating whether unsynchronization is used.
    * @see #isUnsynchronization()
    * @see <a href="http://www.id3.org/id3v2.3.0#sec5">ID3v2.3 unsynchronization scheme</a>
    */
   public void setUnsynchronization(boolean unsynchronization)
   {
      if (this.unsynchronization != unsynchronization)
      {
         this.unsynchronization = unsynchronization;
         dirty = true;
      }
   }

   /**
    * Inidcates whether the optional extended header is present.  If it is, you may then acess the <i>extendedHeaderSize</i>, <i>CRCDataPresent</i>, <i>paddingSize</i>, and
    * optionally the <i>CRCData</i> fields.
    * @return whether the optional extended header is present.
    * @see #setExtendedHeaderPresent(boolean)
    */
   public boolean isExtendedHeaderPresent()
   {
      return extendedHeaderPresent;
   }

   /**
    * sets whether the optional extended header is present.  If false, all extended header fields (<i>paddingSize</i> and <i>CRCData</i>) are erased.  If true, then
    * subsequent calls to the {@link #setCRCData(byte[])} and {@link #setPaddingSize(int)} are enabled.
    * @param extendedHeaderPresent boolean indicating whether the optional extended header is present.
    * @see #isExtendedHeaderPresent()
    */
   public void setExtendedHeaderPresent(boolean extendedHeaderPresent)
   {
      if (this.extendedHeaderPresent != extendedHeaderPresent)
      {
         this.extendedHeaderPresent = extendedHeaderPresent;
         if (!extendedHeaderPresent)
         {
            extendedHeaderSize = 0;
            CRCDataPresent     = false;
            paddingSize        = 0;
            CRCData            = new byte[0];
         }
         dirty = true;
      }
   }

   /**
    * Indicates whether the ID3v2.3 tag is in an experimental stage.  I have no idea why this is useful.
    * @return whether the ID3v2.3 tag is in an experimental stage.
    * @see #setExperimentalIndicator(boolean)
    */
   public boolean isExperimentalIndicator()
   {
      return experimentalIndicator;
   }

   /**
    * sets whether the tag is in an experimental stage.
    * @param experimentalIndicator boolean indicating whether the tag is in an experimental stage.
    * @see #isExperimentalIndicator()
    */
   public void setExperimentalIndicator(boolean experimentalIndicator)
   {
      if (this.experimentalIndicator != experimentalIndicator)
      {
         this.experimentalIndicator = experimentalIndicator;
         dirty = true;
      }
   }

   /**
    * while {@link #getTagSize()} returns the value of the <i>tagSize</i> field stored in the tag header (which does not include the size of the tag header), this method
    * returns the actual total number of bytes in the tag.  To clarify, the {@link #getTagSize()} method returns the total tag size - 10 bytes, while this method returns
    * the total tag size.  I know its weird and confusing, but there's a reason for it.
    * @return the size of the tag header and the extended header, if present.
    */
   public int getSize()
   {
      return header.length;
   }

   /**
    * gets the size of the ID3v2.3 tag excluding the tag header.
    * @return the size of the tag excluding the standard tag header.  That is, total tag size - 10.
    * @see #setTagSize(int)
    * @see #getSize()
    */
   public int getTagSize()
   {
      return tagSize;
   }

   /**
    * sets the size of the tage excluding this tag header, ie (total tag size - 10).
    * @param tagSize the size of the tage excluding the tag header.
    * @see #getTagSize()
    */
   public void setTagSize(int tagSize)
   {
      if (tagSize < 0)
         throw new IllegalArgumentException("Invalid tag size, " + tagSize + ". It must be > 0.");

      this.tagSize = tagSize;
   }

   /**
    * gets the size of the padding which is an area filled with 0's and is found after the end of the ID3v2.3 tag and before the actual audio portion of the .mp3 file.
    * @return the size of the padding which is found at the end of the ID3v2.3 tag.
    * @exception IllegalStateException  if the <i>extendedHeaderPresent</i> flag is not true.  The <i>extendedHeaderPresent</i> flag must be true in order to call this method.
    * @see #setPaddingSize(int)
    */
   public int getPaddingSize() throws IllegalStateException
   {
      if (!extendedHeaderPresent)
         throw new IllegalStateException("The padding size may not be read from the ID3v2.3 extended tag header when the extendedHeaderPresent flag is false.");

     return paddingSize;
   }

   /**
    * sets the size of the padding which is an area filled with 0's and found after the end of the ID3v2.3 tag and before the actual audio portion of the .mp3 file.
    * @param paddingSize   the size of the padding.
    * @exception IllegalStateException  if the <i>extendedHeaderPresent</i> flag is not true.  The <i>extendedHeaderPresent</i> flag must be true in order to call this method.
    * @see #getPaddingSize()
    */
   public void setPaddingSize(int paddingSize) throws IllegalStateException
   {
      if (!extendedHeaderPresent)
         throw new IllegalStateException("The padding size may not be set in the ID3v2.3 extended tag header when the extendedHeaderPresent flag is false.");
      if (paddingSize < 0)
         throw new IllegalArgumentException("Invalid padding size, " + paddingSize + ". It must be > 0.");

      this.paddingSize = paddingSize;
      dirty            = true;
   }

   /**
    * gets the CRC data.
    * @return the CRC data.
    * @exception IllegalStateException  if the <i>extendedHeaderPresent</i> flag is not true.  The <i>extendedHeaderPresent</i> flag must be true in order to call this method.
    * @see #setCRCData(byte[])
    */
   public byte[] getCRCData() throws IllegalStateException
   {
      if (!extendedHeaderPresent)
         throw new IllegalStateException("CRC Data may not be read from the ID3v2.3 extended tag header when the extendedHeaderPresent flag is false.");

     return CRCData;
   }

   /**
    * sets the CRC data.
    * @param CRCData   the CRC data.
    * @exception IllegalStateException  if the <i>extendedHeaderPresent</i> flag is not true.  The <i>extendedHeaderPresent</i> flag must be true in order to call this method.
    * @see #getCRCData()
    */
   public void setCRCData(byte[] CRCData) throws IllegalStateException
   {
      if (!extendedHeaderPresent)
         throw new IllegalStateException("CRC Data may not be set in the ID3v2.3 extended tag header when the extendedHeaderPresent flag is false.");

      if (CRCData == null || CRCData.length == 0)
      {
         this.CRCData   = new byte[0];
         CRCDataPresent = false;
      }
      else if (CRCData.length == ID3v2_3_TAG_HEADER_EXTENDED_HEADER_CRC_DATA_SIZE)
      {
         this.CRCData   = CRCData;
         CRCDataPresent = true;
      }
      else
      {
         throw new IllegalArgumentException("Invalid CRC data, " + CRCData.length + ".  It must be 4 bytes long.");
      }
      dirty = true;
   }

   /**
    * if the tag header's values have been modified, then resize the raw binary buffer and store the new values there.
    * When finished, reset the dirty flag to indicate that the buffer is up to date, and the tag header is now ready to be saved to the .mp3 file.
    */
   public void setBuffer()
   {
      int tagHeaderSize = ID3V2_3_TAG_HEADER_STANDARD_SIZE + (extendedHeaderPresent ? ID3V2_3_TAG_HEADER_EXTENDED_HEADER_DEFAULT_SIZE  : 0) +
                                                             (CRCDataPresent        ? ID3v2_3_TAG_HEADER_EXTENDED_HEADER_CRC_DATA_SIZE : 0);

      header = new byte[tagHeaderSize];
      header[0] = 0x49;   // I
      header[1] = 0x44;   // D
      header[2] = 0x33;   // 3
      header[3] = 0x03;   // version 3
      header[4] = 0x00;   //         0
      header[5] = (byte)(unsynchronization     ? header[5] | ID3v2_3_TAG_HEADER_UNSYNCHRONIZATION_MASK       : header[5] & ~ID3v2_3_TAG_HEADER_UNSYNCHRONIZATION_MASK      );
      header[5] = (byte)(extendedHeaderPresent ? header[5] | ID3v2_3_TAG_HEADER_EXTENDED_HEADER_PRESENT_MASK : header[5] & ~ID3v2_3_TAG_HEADER_EXTENDED_HEADER_PRESENT_MASK);
      header[5] = (byte)(experimentalIndicator ? header[5] | ID3v2_3_TAG_HEADER_EXPERIMENTAL_INDICATOR_MASK  : header[5] & ~ID3v2_3_TAG_HEADER_EXPERIMENTAL_INDICATOR_MASK );
      header[6] = (byte)((tagSize & 0x0FE00000) >> 21);
      header[7] = (byte)((tagSize & 0x001FC000) >> 14);
      header[8] = (byte)((tagSize & 0x00003F80) >> 7);
      header[9] = (byte) (tagSize & 0x0000007F);

      // if there are any additional fields, then write them to the raw binary header
      if (header.length != ID3V2_3_TAG_HEADER_STANDARD_SIZE)
      {
         extendedHeaderSize = CRCDataPresent ? 10 : 6;

         header[10] = (byte)((extendedHeaderSize & 0xFF000000) >> 24);
         header[11] = (byte)((extendedHeaderSize & 0x00FF0000) >> 16);
         header[12] = (byte)((extendedHeaderSize & 0x0000FF00) >> 8);
         header[13] = (byte) (extendedHeaderSize & 0x000000FF);
         header[14] =        (CRCDataPresent ? ID3v2_3_TAG_HEADER_EXTENDED_HEADER_CRC_MASK : 0x00);
         header[15] = 0x00;
         header[16] = (byte)((paddingSize & 0xFF000000) >> 24);
         header[17] = (byte)((paddingSize & 0x00FF0000) >> 16);
         header[18] = (byte)((paddingSize & 0x0000FF00) >> 8);
         header[19] = (byte) (paddingSize & 0x000000FF);

         if (CRCDataPresent)
            System.arraycopy(CRCData, 0, header, 20, CRCData.length);
      }
      dirty = false;
   }

   /**
    * save the ID3v2.3 tag header to the .mp3 file.
    * @param outputStream   output stream pointing to the starting location of the ID3v2.3 tag header within the .mp3 file.
    * @throws IOException   if there was an error writing the ID3v2.3 tag header to the .mp3 file.
    */
   public void save(OutputStream outputStream) throws IOException
   {
      outputStream.write(header);
      dirty = false;
   }

   /**
    * save the ID3v2.3 tag header to the .mp3 file.
    * @param file   random access file pointing to the starting location of the ID3v2.3 tag header within the .mp3 file.
    * @throws IOException   if there was an error writing the ID3v2.3 tag header to the .mp3 file.
    */
   public void save(RandomAccessFile file) throws IOException
   {
      file.write(header);
      dirty = false;
   }

   /**
    * gets a string representation of the ID3v2.3 tag header showing the values of all the tag header's fields as well as the extended header's fields if it is present.
    * @return a string representation of the ID3v2.3 tag header.
    */
   public String toString()
   {
      StringBuffer buffer = new StringBuffer();

      buffer.append("ID3v2.3 tag header\n");
      buffer.append("   bytes.............................: " +  header.length        + " bytes\n");
      buffer.append("                                       " +  ID3v23FrameBodyUtility.hex(header, 38) + "\n");
      buffer.append("   version...........................: " + "2.3.0"               + "\n");
      buffer.append("   unsynchronization.................: " + unsynchronization     + "\n");
      buffer.append("   extended header present...........: " + extendedHeaderPresent + "\n");
      buffer.append("   experimental indicator............: " + experimentalIndicator + "\n");
      buffer.append("   tag size..........................: " + tagSize               + " bytes\n");
      buffer.append("   extended header - size............: " + extendedHeaderSize    + "\n");
      buffer.append("   extended header - CRC data present: " + CRCDataPresent        + "\n");
      buffer.append("   extended header - padding size....: " + paddingSize           + "\n");
      buffer.append("   extended header - CRC data........: " + (CRCData.length == 0 ? "none" : ID3v23FrameBodyUtility.hex(CRCData, 0)) + "\n");

      return buffer.toString();
   }
}
