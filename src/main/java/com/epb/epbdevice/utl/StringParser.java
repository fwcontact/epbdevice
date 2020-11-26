package com.epb.epbdevice.utl;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class StringParser {

    private static final Log LOG = LogFactory.getLog(StringParser.class);
    private static final String EMPTY = "";
    private static final String COMMA = ",";
    private static final String YES = "Y";
    private static final String RIGHT = "R";
    private static final String CENTER = "C";
    private static final String LEFT = "L";
    
    private int currentPosition;
    private int maxPosition;
    private String str;

    /**
     * Creates a new instance of EpbPosStringParser
     * @param str
     */
    public StringParser(String str) {
        this.str = str;
        currentPosition = 0;
        maxPosition = str == null ? 0 : str.length();
    }

    public String nextToken(char c) {
        if (currentPosition < maxPosition) {

            int start = currentPosition;
            while (currentPosition < maxPosition && c != str.charAt(currentPosition)) {
                currentPosition++;
            }

            if (currentPosition < maxPosition) {
                return str.substring(start, currentPosition++);
            } else {
                return str.substring(start);
            }
        } else {
            return EMPTY;
        }
    }

    //c#版本中的截取函数
    public static String getSubString(String input, int length) {
        try {
            String subString = input;
//            int stringLengthChinese = CharToByteConverter.getDefault().convertAll(input.toCharArray()).length;
            int stringLengthChinese = getStringLengthChinese(input);
            int stringLength = input.length();

            //如果字符串不够长
            if (stringLengthChinese <= length) {
                return input;
            }

            //如果太长
            //如果没有汉字
            if (stringLengthChinese == stringLength) {
                return input.substring(0, length - 1);
            }

            //如果有汉字的，先截取
//            while (CharToByteConverter.getDefault().convertAll(subString.toCharArray()).length > length) {
            while (getStringLengthChinese(subString) > length) {
                subString = subString.substring(0, subString.length() - 1);
            }
            return subString;
        } catch (Throwable ex) {
            return input;
        }
    }

    //对齐
    //pAlign  = C L R
    //暂时一律截断
    public static String setStringAlignment(String input, String align, int length, String breakFlg, String emptyFlg) {
        try {
            String subString = input;
            int leftLength = 0;
            int rightLength = 0;

//            int stringLengthChinese = CharToByteConverter.getDefault().convertAll(subString.toCharArray()).length;
            int stringLengthChinese = getStringLengthChinese(subString);

            //如果字符串够长
            if (stringLengthChinese >= length) {
                //如果过长，一律截断
                subString = splitString(input, length, EMPTY);
                //截取后，有可能多一个空格
//                stringLengthChinese = CharToByteConverter.getDefault().convertAll(subString.toCharArray()).length;
                stringLengthChinese = getStringLengthChinese(subString);
            }

            if (CENTER.equals(align)) {
                //如果不够长
                //subString = alignCenter(subString, length);
                leftLength = (length - stringLengthChinese) / 2;
                rightLength = length - leftLength - stringLengthChinese;
                if (YES.equals(emptyFlg)) {
                    subString = getWhiteString(leftLength) + subString + getWhiteString(rightLength);
                }
            } else if (LEFT.equals(align)) {
                //subString = alignLeft(subString, length);
                rightLength = length - stringLengthChinese;
                if (YES.equals(emptyFlg)) {
                    subString = subString + getWhiteString(rightLength);
                }
            } else if (RIGHT.equals(align)) {
                //subString = alignRight(subString, length);
                //如果不够长
                leftLength = length - stringLengthChinese;
                if (YES.equals(emptyFlg)) {
                    subString = getWhiteString(leftLength) + subString;
                }
            }
            return subString;
        } catch (Exception ex) {
            return input;
        }
    }

    public static String getTruncString(String input, String align, int length) {
        try {
            String subString = input;

//            int stringLengthChinese = CharToByteConverter.getDefault().convertAll(subString.toCharArray()).length;
            int stringLengthChinese = getStringLengthChinese(subString);

            //如果字符串够长
            if (stringLengthChinese >= length) {
                //如果过长，一律截断
                subString = splitString(input, length, EMPTY);
                return input.replaceFirst(subString, EMPTY);
            }
            return EMPTY;
        } catch (Exception ex) {
            return EMPTY;
        }
    }

    public static String getSplitString(String input) {
        try {
            if (input == null) {
                return EMPTY;
            }
            if (input.length() > 0 && input.indexOf(COMMA) == -1) {
                input += COMMA;
            }
            String result = EMPTY;
            String[] splitArray = null;
            int temp = 0;
            splitArray = input.split(COMMA);
            int count = splitArray.length;
            for (int i = 0; i < count; i++) {
                try {
                    temp = Integer.parseInt(splitArray[i]);
                    result = result + String.valueOf((char) temp);
                } catch (NumberFormatException ex) {
                    System.out.println("com.epb.epbdevice.utl.StringParser.getSplitString()" + "->" + ex.getMessage());
                }
            }
            return result;
        } catch (Throwable ex) {
//            EpbExceptionMessenger.showExceptionMessage(ex);
//            EpbSimpleMessenger.showSimpleMessage("Split Failed!");
            LOG.error("com.epb.epbdevice.utl.StringParser.getSplitString()", ex);
            return EMPTY;
        }
    }

    public static String getSplitAsciiString(String input, String splitSign) {
        try {
            if (input == null) {
                return EMPTY;
            }
            if (input.length() > 0 && input.indexOf(splitSign) == -1) {
                input += splitSign;
            }
            String result = EMPTY;
            String[] splitArray = null;
            int temp = 0;
            splitArray = input.split(splitSign);
            int count = splitArray.length;
            for (int i = 0; i < count; i++) {
                try {
                    temp = Integer.parseInt(splitArray[i], 16);
                    result = result + String.valueOf((char) temp);
                } catch (NumberFormatException ex) {
                    LOG.error("com.epb.epbdevice.utl.StringParser.getSplitAsciiString()", ex);
                }
            }
            return result;
        } catch (Throwable ex) {
//            EpbExceptionMessenger.showExceptionMessage(ex);
//            EpbSimpleMessenger.showSimpleMessage("Split Failed!");
            LOG.error("com.epb.epbdevice.utl.StringParser.getSplitAsciiString()", ex);
            return EMPTY;
        }
    }

    /**

     * 字符串按字节截取

     * @param str 原字符

     * @param len 截取长度

     * @return String


     */
    public static String splitString(String str, int len) {
        return splitString(str, len, "...");
    }

    /**

     * 字符串按字节截取

     * @param str 原字符

     * @param len 截取长度

     * @param elide 省略符

     * @return String


     */
    public static String splitString(String str, int len, String elide) {

        if (str == null) {
            return EMPTY;
        }

        byte[] strByte = str.getBytes();
        int strLen = strByte.length;
        int elideLen = (elide.trim().length() == 0) ? 0 : elide.getBytes().length;
        if (len >= strLen || len < 1) {
            return str;
        }

        if (len - elideLen > 0) {
            len = len - elideLen;
        }

        int count = 0;
        for (int i = 0; i < len; i++) {
            int value = (int) strByte[i];
            if (value < 0) {
                count++;
            }
        }

        if (count % 2 != 0) {
            len = (len == 1) ? len + 1 : len - 1;
        }

        return new String(strByte, 0, len) + elide.trim();
    }

    // customize for UTF-8, 3 byte
    public static int getLengthStringUtf8(String str) {
        try {
            if (str == null) {
                return 0;
            }
            return str.getBytes("UTF-8").length;

        } catch (UnsupportedEncodingException throwable) {
            return 0;
        }
    }

    public static String splitStringUtf8(String str, int len) {
        try {
            if (str == null) {
                return EMPTY;
            }

            byte[] strByte = str.getBytes();
            int strLen = strByte.length;
            if (len >= strLen || len < 1) {
                return str;
            }
            
            int newLen = 0;
            for (int i = 0; i < len; i++) {
                int value = (int) strByte[i];
                if (value < 0) {
                    if (newLen + 3 > len) {
                        break;
                    }
                    newLen = newLen + 3;
                } else {
                    if (newLen + 1 > len) {
                        break;
                    }
                    newLen = newLen + 1;
                }
            }

            return new String(strByte, 0, newLen);
        } catch (Throwable throwable) {
            return str;
        }
    }

    public static String getWhiteString(int size, char whiteChar) {
        char[] cFill = new char[size];
        for (int i = 0; i < size; i++) {
            cFill[i] = whiteChar;
        }
        return new String(cFill);
    }

    public static String getWhiteString(int size) {

        return getWhiteString(size, ' ');
    }

    public static String setNoFormat(int v, BigInteger scale) {
        String result = v + EMPTY;
        int resultLenght = result.length();
        for (int i = 0; i < scale.intValue() - resultLenght; i++) {
            result = "0" + result;
        }
        return result;
    }

    public static String lpad(String str, int length, Character chr) {
        if (chr == null || length <= 0) {
            return str;
        } else {
            str = str == null ? EMPTY : str;
            final int l = str.length();
            if (l >= length) {
                return str.substring(0, length);
            }
            String retStr = str;
            for (int i = l; i < length; i++) {
                retStr = chr + retStr;
            }
            return retStr;
        }
    }

    public static String rpad(String str, int length, Character chr) {
        if (chr == null || length <= 0) {
            return str;
        } else {
            str = str == null ? EMPTY : str;
            final int l = str.length();
            if (l >= length) {
                return str.substring(0, length);
            }
            String retStr = str;
            for (int i = l; i < length; i++) {
                retStr = retStr + chr;
            }
            return retStr;
        }
    }

    public static int getStringLengthChinese(String str) {
        //    return CharToByteConverter.getDefault().convertAll(subString.toCharArray()).length;  // jdk1.8 remove CharToByteConverter class
        if (str == null || str.length() == 0) {
            return 0;
        }
        byte[] buffer = str.getBytes();
        int length = buffer.length;
        buffer = null;
        return length;
    }
}
