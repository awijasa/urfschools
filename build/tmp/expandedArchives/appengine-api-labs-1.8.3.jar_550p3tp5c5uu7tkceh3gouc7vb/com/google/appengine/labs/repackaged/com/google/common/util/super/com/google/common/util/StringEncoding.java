/*
 * Copyright 2005 Google Inc.
 * All Rights Reserved.
 */

package com.google.common.util;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Utility class for encoding strings to and from byte arrays.
 *
 * @author Neal Gafter
 */
@GwtCompatible(emulated = true)
public class StringEncoding {
  private final char[] DIGITS;
  private final int SHIFT;
  private final int MASK;
  private final Map<Character, Integer> CHAR_MAP =
      new HashMap<Character, Integer>();
  private static byte buffer160[] = new byte[20];
  private static byte buffer128[] = new byte[16];

  /**
   * Creates a new encoding based on the supplied set of digits.
   *
   * The number of digits must be a power of 2, at least 2, and at most 128.
   * The digits are sorted and duplicates are removed, so
   *   new StringEncoding("bananas".toCharArray())
   * is equivalent to
   *   new StringEncoding("abns".toCharArray())
   */
  public StringEncoding(char[] userDigits) {
    this(userDigits, true);
  }

  /**
   * @param userDigits The input digits to be used in the encoding.
   * @param sortDigits True if the input digits should be sorted. False if
   *                     it should go in insertion order.
   */
  StringEncoding(final char[] userDigits, boolean sortDigits) {
    int base = userDigits.length;
    checkArgument(base >= 2, "Too few digits");
    checkArgument(base < 256, "Too many digits");

    this.SHIFT = Integer.numberOfTrailingZeros(base);
    checkArgument(1 << SHIFT == base, "Not a power of 2: " + base);

    this.MASK = base - 1;

    Set<Character> t;
    if (sortDigits) {
      t = new TreeSet<Character>();
    } else {
      t = new LinkedHashSet<Character>();
    }
    for (char c : userDigits) {
      checkArgument(t.add(c), "Duplicate digit: " + c);
    }

    char[] digits = new char[base];
    int i = 0;
    for (char c : t) {
      CHAR_MAP.put(c, i);
      digits[i++] = c;
    }
    this.DIGITS = digits;
  }

  /** Returns the given bytes in their encoded form. */
  public String encode(byte[] data) {
    if (data.length == 0) {
      return "";
    }

    // SHIFT is the number of bits per output character, so the length of the
    // output is the length of the input multiplied by 8/SHIFT, rounded up.
    if (data.length >= (1 << 28)) {
      // The computation below will fail, so don't do it.
      throw new IllegalArgumentException();
    }
    int outputLength = (data.length * 8 + SHIFT - 1) / SHIFT;
    StringBuilder result = new StringBuilder(outputLength);

    int buffer = data[0];
    int next = 1;
    int bitsLeft = 8;
    while (bitsLeft > 0 || next < data.length) {
      if (bitsLeft < SHIFT) {
        if (next < data.length) {
          buffer <<= 8;
          buffer |= (data[next++] & 0xff);
          bitsLeft += 8;
        } else {
          int pad = SHIFT - bitsLeft;
          buffer <<= pad;
          bitsLeft += pad;
        }
      }
      int index = MASK & (buffer >> (bitsLeft - SHIFT));
      bitsLeft -= SHIFT;
      result.append(DIGITS[index]);
    }
    // assert result.length() == outputLength;  // Bug in this function if not
    return result.toString();
  }

  /**
   * Decodes the given encoded string and returns the original raw bytes;
   * does not throw an exception on bad input, instead having undefined
   * behavior.
   */
  public byte[] unsafeDecodeBinary(String encoded) {
    if (encoded.length() == 0) {
      return new byte[0];
    }
    int encodedLength = encoded.length();
    int outLength = encodedLength * SHIFT / 8;
    byte[] result = new byte[outLength];
    int buffer = 0;
    int next = 0;
    int bitsLeft = 0;
    for (char c : encoded.toCharArray()) {
      // Unrecognized characters will default to -1. This is to preserve
      // backward compatibility. Use decodeBinary() to predictably handle
      // illegal characters.
      int value = -1;
      if (CHAR_MAP.containsKey(c)) {
        value = CHAR_MAP.get(c);
      }
      buffer <<= SHIFT;
      buffer |= value & MASK;
      bitsLeft += SHIFT;
      if (bitsLeft >= 8) {
        result[next++] = (byte) (buffer >> (bitsLeft - 8));
        bitsLeft -= 8;
      }
    }
    // assert next == outLength && bitsLeft < SHIFT;
    return result;
  }

  /** Decodes the given encoded string and returns the original raw bytes. */
  public byte[] decodeBinary(String encoded) {
    if (encoded.length() == 0) {
      return new byte[0];
    }
    int encodedLength = encoded.length();
    int outLength = encodedLength * SHIFT / 8;
    byte[] result = new byte[outLength];
    int buffer = 0;
    int next = 0;
    int bitsLeft = 0;
    for (char c : encoded.toCharArray()) {
      buffer <<= SHIFT;
      Integer charIndex = CHAR_MAP.get(c);
      if (charIndex == null) {
        throw new IllegalArgumentException(encoded);
      }
      buffer |= charIndex & MASK;
      bitsLeft += SHIFT;
      if (bitsLeft >= 8) {
        result[next++] = (byte) (buffer >> (bitsLeft - 8));
        bitsLeft -= 8;
      }
    }
    checkArgument(next == outLength && bitsLeft < SHIFT, encoded);
    return result;
  }

  /*
   * TODO(cpovirk): use Charsets and Preconditions if we can change the Android
   * version of this target to depend on Guava rather than
   * //java/com/google/common/...
   */

  private static final Charset UTF_8 = Charset.forName("UTF-8");

  private static void checkArgument(boolean expression, String errorMessage) {
    if (!expression) {
      throw new IllegalArgumentException(errorMessage);
    }
  }
}

