package com.beaglebuddy.mp3.id3v23.frame_body;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.beaglebuddy.mp3.pojo.Price;





/**
 * <table class="logos_width">
 *    <tbody>
 *       <tr>
 *          <td                               ><img src="../../../../../resources/id3v2.gif"                     width="56"  height="54"  alt="ID3 logo"        /></td>
 *          <td class="logos_horz_align_right"><img src="../../../../../resources/beaglebuddy_software_logo.gif" width="340" height="110" alt="Beaglebuddy logo"/></td>
 *       </tr>
 *    </tbody>
 * </table>
 * Base class for the body of ID3V2.3 frames.  This class is simply a collection of utility methods and is not a frame body type.
 * @see <a href="http://id3.org/id3v2.3.0"         target="_blank">ID3 tag version 2.3.0 standard</a>
 * @see <a href="http://en.wikipedia.org/wiki/ID3" target="_blank">wikipedia history of ID3 tags</a>
 */
public class ID3v23FrameBodyUtility
{
   // class mnemonics
   private static final Charset CHARSET_ISO_8859_1 = Charset.forName("ISO-8859-1");
   private static final Charset CHARSET_UTF_16     = Charset.forName("UTF-16");

   // utility members
   protected int nullTerminatorIndex;        // used by derived classes to find the end of a string in the data buffer.
   protected int nextNullTerminatorIndex;    // used by derived classes to find the end of a string in the data buffer.




   /**
    * default constructor.
    */
   public ID3v23FrameBodyUtility()
   {
      // no code necessary
   }

   /**
    * formats a date as YYYYMMDD.
    * @param date the date to format.  If null, then the today's date is used.
    * @return the specified date formatted as YYYYMMDD.
    */
   public static String formateDate(Date date)
   {
      SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");

      return formatter.format(date == null ? new Date() : date);
   }

   /**
    * finds the next null terminating character in the raw data stream according to the specified character encoding.
    * A null character is represented as a single 0x00 byte using the ISO 8859-1 encoding, and as two 0x00 0x00 bytes using UTF-16 encoding.
    * @param buffer         the raw binary bytes in which to find the next null terminator character.
    * @param startingFrom   the index at which to start searching from in the data.
    * @param charset        the character set used to encode the string (and hence determine the null terminating character).
    * <br/><br/>
    * @return the index of the next null terminator in the data.
    */
   public static int getNextNullTerminator(byte[] buffer, int startingFrom, Charset charset)
   {
      int index=0;
      if (charset.equals(CHARSET_ISO_8859_1))
      {
         for(index=startingFrom; index<buffer.length && buffer[index] != 0; ++index);
      }
      else if (charset.equals(CHARSET_UTF_16))
      {
         for(index=startingFrom; index+1<buffer.length && !(buffer[index] == 0 && buffer[index+1] == 0); index += 2);
      }
      return index;
   }

   /**
    * formats a single byte as a 2 digit hex value, ie, 0xbb, where 0 < b < F.<br/>
    * example: decimal: 12  -> hex: 0C<br/>
    * example: decimal: 178 -> hex: B2<br/>
    * @param data  byte data whose value will be converted to hex.
    * <br/><br/>
    * @return the hex representation of a byte.
    */
   public static String hex(byte data)
   {
      StringBuffer buffer  = new StringBuffer();
      char[]       hexChar = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

      buffer.append("0x");
      buffer.append(hexChar[(byte)((data & 0xF0) >> 4)]);
      buffer.append(hexChar[(byte)((data & 0x0F)     )]);

      return buffer.toString();
   }

   /**
    * formats raw binary byte data as a sequence of bytes in the format 0xbb 0xbb ... with 16 bytes per line.
    * @param data     raw binary data to be printed.
    * @param indent   number of spaces to indent each line of output before displaying the hex bytes.
    * <br/><br/>
    * @return a string representation of the binary data as hexadecimal bytes.
    */
   public static String hex(byte[] data, int indent)
   {
      StringBuffer buffer = new StringBuffer();

      // format the hex data
      for(int i=0; i<data.length; ++i)
      {
         if (i!=0 && i%16==0)
         {
            buffer.append("\n");
            buffer.append(pad(indent)); // indent the specified number of spaces
         }
         buffer.append(hex(data[i]));
         buffer.append(" ");
      }
      return buffer.toString();
   }

   /**
    * gets a string consisting of the specified number of blank spaces which can be used for padding (left or right) in order to format some output (ie, make columns line up, etc).
    * @return a string of spaces.
    * @param numSpaces number of spaces to return.
    */
   public static String pad(int numSpaces)
   {
      StringBuffer buffer = new StringBuffer();

      for(int i=0; i<numSpaces; ++i)
         buffer.append(" ");

      return buffer.toString();
   }

   /**
    * convert a list of prices to a string, with each price separated by the "/" character.<br/>
    * example: USD0.99/EUR1.00/GBP0.65<br/>
    * @param prices   list of prices.
    * @return a string representation of the list of price(s).
    */
   public static String pricesToString(List<Price> prices)
   {
      StringBuffer priceString = new StringBuffer();
      for (Price price : prices)
         priceString.append((priceString.length() == 0 ? "" : "/") + price);

      return priceString.toString();
   }

   /**
    * converts an integer value to a 4 byte array.
    * @return a 4 byte array holding the integer value.
    * @param n  the integer value to be converted to a byte array.
    */
   public static byte[] intToBytes(int n)
   {
      byte[] buffer = new byte[4];

      buffer[0] = (byte)((n & 0xFF000000) >> 24);
      buffer[1] = (byte)((n & 0x00FF0000) >> 16);
      buffer[2] = (byte)((n & 0x0000FF00) >> 8);
      buffer[3] = (byte) (n & 0x000000FF);

      return buffer;
   }

   /**
    * converts a short value to a 2 byte array.
    * @return a 2 byte array holding the short value.
    * @param n  the short value to be converted to a byte array.
    */
   public static byte[] shortToBytes(int n)
   {
      byte[] buffer = new byte[2];

      buffer[0] = (byte)((n & 0xFF00) >> 8);
      buffer[1] = (byte) (n & 0x00FF);

      return buffer;
   }

   /**
    * converts a String to a byte array using a specified character set encoding.  The String class has a getBytes() method, but that method does
    * not include a null terminator in the byte array that it returns.  This method does.
    * @return a byte array holding the string value in the specified encoding with null terminating bytes.
    * @param encoding   the encoding to use when converting the string to a byte array.
    * @param string     the string to be converted to a byte array.
    */
   public static byte[] stringToBytes(Charset encoding, String string)
   {
      byte[] data = string.getBytes(encoding);
      byte[] bytes = new byte[data.length + (encoding.equals(CHARSET_ISO_8859_1) ? 1 : 2)];
      System.arraycopy(data, 0, bytes, 0, data.length);
      bytes[data.length] = (byte)0x00;
      if (encoding.equals(CHARSET_UTF_16))
         bytes[data.length+1] = (byte)0x00;

      return bytes;
   }
}
