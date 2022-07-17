/*
 * Copyright (C) 2010 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.common.base;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.logging.Level.WARNING;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.errorprone.annotations.InlineMe;
import com.google.errorprone.annotations.InlineMeValidationDisabled;
import java.util.logging.Logger;
import javax.annotation.CheckForNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Static utility methods pertaining to {@code String} or {@code CharSequence} instances.
 *
 * @author Kevin Bourrillion
 * @since 3.0
 */
@GwtCompatible
@ElementTypesAreNonnullByDefault
public final class Strings {
  /**
   * 私有化构造方法：这是一个工具类，不对外提供创建实例的构造方法
   */
  private Strings() {}

  /**
   * Returns the given string if it is non-null; the empty string otherwise.
   *    1、如果给定的字符串为null，则返回空字符串;
   *    2、如果给定的字符串不为null，则返回原字符串。
   *    Tip：其实这个方法只是针对 string==null 的情况才会处理，其余情况不会处理
   *
   * @param string the string to test and possibly return
   * @return {@code string} itself if it is non-null; {@code ""} if it is null
   */
  public static String nullToEmpty(@CheckForNull String string) {
    return Platform.nullToEmpty(string);
    // 底层调用了 Platform 类的静态实现，实现逻辑如下：
    /*
      static String nullToEmpty(@CheckForNull String string) {
        return (string == null) ? "" : string;
      }
    */
  }

  /**
   * Returns the given string if it is nonempty; {@code null} otherwise.
   *    1、如果入参 为空 或 null，则返回 null，
   *    2、否则返回原字符串
   *     Tip：其实这个方法只是针对 string==null 或 string 为空 的情况才会处理，其余情况不会处理
   * @param string the string to test and possibly return
   * @return {@code string} itself if it is nonempty; {@code null} if it is empty or null
   */
  @CheckForNull
  public static String emptyToNull(@CheckForNull String string) {
    return Platform.emptyToNull(string);
    /*
    * 调用逻辑：
    *     1、Platform.emptyToNull(string);
                static String emptyToNull(@CheckForNull String string) {
                  return stringIsNullOrEmpty(string) ? null : string;
                }
    *     2、stringIsNullOrEmpty(string) ? null : string;
    *
    * stringIsNullOrEmpty(string) 方法的实现逻辑：
    *     // 如果入参为 null 或 为空，则返回 true，否则返回 false
          static boolean stringIsNullOrEmpty(@CheckForNull String string) {
            return string == null || string.isEmpty();
          }
    *  */
  }

  /**
   * Returns {@code true} if the given string is null or is the empty string.
   *  如果给入参字符串为null或为空字符串，则返回 true
   *
   * <p>Consider normalizing your string references with {@link #nullToEmpty}. If you do, you can
   * use {@link String#isEmpty()} instead of this method, and you won't need special null-safe forms
   * of methods like {@link String#toUpperCase} either. Or, if you'd like to normalize "in the other
   * direction," converting empty strings to {@code null}, you can use {@link #emptyToNull}.
   *
   *  建议使用 {@link #nullToEmpty} 方法来规范化你字符串引用。如果你提前这样做了，就可以直接使用 {@link String#isEmpty()} 方法
   *  来代替 {@link #isNullOrEmpty} 方法（因为如果入参为 null，在经过上一步后已经被转变成了空字符串）。
   *  或者，你也可以从另一个角度来规范化字符串，比如提前将空字符串转为null（使用 {@link #emptyToNull}）
   * 
   * @param string a string reference to check
   * @return {@code true} if the string is null or is the empty string
   */
  public static boolean isNullOrEmpty(@CheckForNull String string) {
    return Platform.stringIsNullOrEmpty(string);
  }

  /**
   * Returns a string, of length at least {@code minLength}, consisting of {@code string} prepended
   * with as many copies of {@code padChar} as are necessary to reach that length. For example,
   *    判断字符串是否满足给定的最小长度，若不满足则使用指定字符填充到最小要求长度。
   *
   * <ul>
   *   <li>{@code padStart("7", 3, '0')} returns {@code "007"}
   *   <li>{@code padStart("2010", 3, '0')} returns {@code "2010"}
   * </ul>
   *
   * <p>See {@link java.util.Formatter} for a richer set of formatting capabilities.
   *
   * @param string the string which should appear at the end of the result
   * @param minLength the minimum length the resulting string must have. Can be zero or negative, in
   *     which case the input string is always returned.
   * @param padChar the character to insert at the beginning of the result until the minimum length
   *     is reached
   * @return the padded string
   */
  public static String padStart(String string, int minLength, char padChar) {
    /**
     * {@link Preconditions#checkNotNull(Object)}
     */
    // 参数校验：确保方法入参中的对象引用不为空
    checkNotNull(string); // eager for GWT.

    // 如果(入参对象的长度>=限定长度)，无需任何处理（即：如果长度比规定的长，也不用截去多出来的部分）
    if (string.length() >= minLength) {
      return string;
    }
    // 如果(入参对象的长度<限定长度)，创建一个长度为 minLength 的 StringBuilder，计算出待填充字符的数量（minLength-string.length()）
    // 以循环的方式将填充字符追加到 sb 对象开头
    StringBuilder sb = new StringBuilder(minLength);
    for (int i = string.length(); i < minLength; i++) {
      sb.append(padChar);
    }
    // 将原字符串追加到 sb 对象中
    sb.append(string);
    // 返回 sb 对象的字符串形式
    return sb.toString();
  }

  /**
   * Returns a string, of length at least {@code minLength}, consisting of {@code string} appended
   * with as many copies of {@code padChar} as are necessary to reach that length. For example,
   *
   * <ul>
   *   <li>{@code padEnd("4.", 5, '0')} returns {@code "4.000"}
   *   <li>{@code padEnd("2010", 3, '!')} returns {@code "2010"}
   * </ul>
   *
   * <p>See {@link java.util.Formatter} for a richer set of formatting capabilities.
   *
   * @param string the string which should appear at the beginning of the result
   * @param minLength the minimum length the resulting string must have. Can be zero or negative, in
   *     which case the input string is always returned.
   * @param padChar the character to append to the end of the result until the minimum length is
   *     reached
   * @return the padded string
   */
  public static String padEnd(String string, int minLength, char padChar) {
    checkNotNull(string); // eager for GWT.
    if (string.length() >= minLength) {
      return string;
    }
    StringBuilder sb = new StringBuilder(minLength);
    /**
     * 与 {@link #padStart} 方法不同的是，在创建完『StringBuilder sb』对象后，
     * padEnd 方法是将入参字符串追加到 sb 对象中，再使用填充字符追加到 sb 对象中。
     * 而 padStart 方法则正好相反，先将填充字符追加到 sb 对象，再将入参字符串追加到 sb 对象中。
     *
     */
    sb.append(string);
    for (int i = string.length(); i < minLength; i++) {
      sb.append(padChar);
    }
    return sb.toString();
  }

  /**
   * Returns a string consisting of a specific number of concatenated copies of an input string. For
   * example, {@code repeat("hey", 3)} returns the string {@code "heyheyhey"}.
   *
   * <p><b>Java 11+ users:</b> use {@code string.repeat(count)} instead.
   *
   * @param string any non-null string
   * @param count the number of times to repeat it; a nonnegative integer
   * @return a string containing {@code string} repeated {@code count} times (the empty string if
   *     {@code count} is zero)
   * @throws IllegalArgumentException if {@code count} is negative
   */
  @InlineMe(replacement = "string.repeat(count)")
  @InlineMeValidationDisabled("Java 11+ API only")
  public static String repeat(String string, int count) {
    // 参数校验1：空指针校验
    checkNotNull(string); // eager for GWT.

    // 参数校验2：
    if (count <= 1) {
      /**
       * 追踪源码{@link Preconditions#checkArgument(boolean, Object)}可知，
       * 当断言为false时才执行方法中的实际代码，即如果 (count < 0) 时才执行 checkArgument 方法中的代码，抛出 invalid count 异常。
       * 当断言为 true 时，什么也不做。这时候的条件是 (0 <= count <= 1)。
       */
      //
      checkArgument(count >= 0, "invalid count: %s", count);
      // 如果『count==0』，任何字符串乘以0都是空字符串。如果 (0 < count <= 1)，并且 count 还是一个 int 类型，其实就是 count==1,则都按 repeat 一倍来计算
      return (count == 0) ? "" : string;
    }

    // IF YOU MODIFY THE CODE HERE, you must update StringsRepeatBenchmark

    // 计算repeat后，新字符串的长度
    final int len = string.length();
    final long longSize = (long) len * (long) count;
    final int size = (int) longSize;

    // 如果 size 的大小超过 Integer 的最大取值范围，则报错
    if (size != longSize) {
      throw new ArrayIndexOutOfBoundsException("Required array size too large: " + longSize);
    }

    // 如果 size 的大小没有超过 Integer 的最大取值范围，则按 size 的大小创建一个字符数组，并将内容复制到字符数组中
    final char[] array = new char[size];

    // getChars 方法的作用就是将 string 复制到字符数组 array 中，底层是通过 System.arraycopy 实现的
    string.getChars(0, len, array, 0);
    int n;
    for (n = len; n < size - n; n <<= 1) {
      System.arraycopy(array, 0, array, n, n);
    }
    // 当不足以再添加n个元素时跳出循环，再添加（size-n）个元素
    System.arraycopy(array, 0, array, n, size - n);
    // 将字符数组重新包装成字符串对象
    return new String(array);
  }

  /**
   * Returns the longest string {@code prefix} such that {@code a.toString().startsWith(prefix) &&
   * b.toString().startsWith(prefix)}, taking care not to split surrogate pairs. If {@code a} and
   * {@code b} have no common prefix, returns the empty string.
   *    寻找公共前缀
   *
   * @since 11.0
   */
  public static String commonPrefix(CharSequence a, CharSequence b) {
    // 参数校验
    checkNotNull(a);
    checkNotNull(b);

    // 公共得前缀允许的最大长度
    int maxPrefixLength = Math.min(a.length(), b.length());

    // 逐一比较获取公共前缀的长度
    int p = 0;
    while (p < maxPrefixLength && a.charAt(p) == b.charAt(p)) {
      p++;
    }

    /**
     *  判断最后两个字符是不是合法的“Java平台增补字符”
     */
    if (validSurrogatePairAt(a, p - 1) || validSurrogatePairAt(b, p - 1)) {
      p--;
    }
    // 返回公共前缀
    return a.subSequence(0, p).toString();
  }

  /**
   * Returns the longest string {@code suffix} such that {@code a.toString().endsWith(suffix) &&
   * b.toString().endsWith(suffix)}, taking care not to split surrogate pairs. If {@code a} and
   * {@code b} have no common suffix, returns the empty string.
   *
   * @since 11.0
   */
  public static String commonSuffix(CharSequence a, CharSequence b) {
    // 参数校验
    checkNotNull(a);
    checkNotNull(b);

    // 获取允许的最长公共后缀
    int maxSuffixLength = Math.min(a.length(), b.length());

    int s = 0;
    // 逐一获取真正的公共后缀
    while (s < maxSuffixLength && a.charAt(a.length() - s - 1) == b.charAt(b.length() - s - 1)) {
      s++;
    }

    // 判断最后两个字符是不是合法的“Java平台增补字符”
    if (validSurrogatePairAt(a, a.length() - s - 1)
        || validSurrogatePairAt(b, b.length() - s - 1)) {
      s--;
    }

    // 返回公共前缀
    return a.subSequence(a.length() - s, a.length()).toString();
  }

  /**
   * True when a valid surrogate pair starts at the given {@code index} in the given {@code string}.
   * Out-of-range indexes return false.
   *    判断最后两个字符是不是合法的“Java平台增补字符”
   *    Java 采用 UTF-16 编码的 Unicode 字符集。UTF-16 使用一个字符（16位）或者二个字符（32位）【多用于一些特殊的中文或日文】表示一个Unicode字符。
   *    对于使用两个字符的，前面一个字符叫 HighSurrogate，后面那个叫 LowSurrogate。
   *    而 char 只占一个字符（16位），所以如果最后一个值占2个字符时会出现 HighSurrogate 相同，而 LowSurrogate 不同的情况，这种情况下就说明他们不是公共字符，
   *    公共前缀的长度需要减一。
   *
   *
   */
  @VisibleForTesting  // 仅测试可见
  static boolean validSurrogatePairAt(CharSequence string, int index) {
    return index >= 0
        && index <= (string.length() - 2)
        && Character.isHighSurrogate(string.charAt(index))
        && Character.isLowSurrogate(string.charAt(index + 1));
  }

  /**
   * Returns the given {@code template} string with each occurrence of {@code "%s"} replaced with
   * the corresponding argument value from {@code args}; or, if the placeholder and argument counts
   * do not match, returns a best-effort form of that string. Will not throw an exception under
   * normal conditions.
   * 返回一个给定模板的字符串
   *
   *
   * <p><b>Note:</b> For most string-formatting needs, use {@link String#format String.format},
   * {@link java.io.PrintWriter#format PrintWriter.format}, and related methods. These support the
   * full range of <a
   * href="https://docs.oracle.com/javase/9/docs/api/java/util/Formatter.html#syntax">format
   * specifiers</a>, and alert you to usage errors by throwing {@link
   * java.util.IllegalFormatException}.
   *
   * <p>In certain cases, such as outputting debugging information or constructing a message to be
   * used for another unchecked exception, an exception during string formatting would serve little
   * purpose except to supplant the real information you were trying to provide. These are the cases
   * this method is made for; it instead generates a best-effort string with all supplied argument
   * values present. This method is also useful in environments such as GWT where {@code
   * String.format} is not available. As an example, method implementations of the {@link
   * Preconditions} class use this formatter, for both of the reasons just discussed.
   *
   * <p><b>Warning:</b> Only the exact two-character placeholder sequence {@code "%s"} is
   * recognized.
   *
   * @param template a string containing zero or more {@code "%s"} placeholder sequences. {@code
   *     null} is treated as the four-character string {@code "null"}.
   * @param args the arguments to be substituted into the message template. The first argument
   *     specified is substituted for the first occurrence of {@code "%s"} in the template, and so
   *     forth. A {@code null} argument is converted to the four-character string {@code "null"};
   *     non-null values are converted to strings using {@link Object#toString()}.
   * @since 25.1
   */
  // TODO(diamondm) consider using Arrays.toString() for array parameters
  public static String lenientFormat(
      @CheckForNull String template, @CheckForNull @Nullable Object... args) {
    template = String.valueOf(template); // null -> "null"

    if (args == null) {
      args = new Object[] {"(Object[])null"};
    } else {
      for (int i = 0; i < args.length; i++) {
        args[i] = lenientToString(args[i]);
      }
    }

    // start substituting the arguments into the '%s' placeholders
    StringBuilder builder = new StringBuilder(template.length() + 16 * args.length);
    int templateStart = 0;
    int i = 0;
    while (i < args.length) {
      int placeholderStart = template.indexOf("%s", templateStart);
      if (placeholderStart == -1) {
        break;
      }
      builder.append(template, templateStart, placeholderStart);
      builder.append(args[i++]);
      templateStart = placeholderStart + 2;
    }
    builder.append(template, templateStart, template.length());

    // if we run out of placeholders, append the extra args in square braces
    if (i < args.length) {
      builder.append(" [");
      builder.append(args[i++]);
      while (i < args.length) {
        builder.append(", ");
        builder.append(args[i++]);
      }
      builder.append(']');
    }

    return builder.toString();
  }

  private static String lenientToString(@CheckForNull Object o) {
    if (o == null) {
      return "null";
    }
    try {
      return o.toString();
    } catch (Exception e) {
      // Default toString() behavior - see Object.toString()
      String objectToString =
          o.getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(o));
      // Logger is created inline with fixed name to avoid forcing Proguard to create another class.
      Logger.getLogger("com.google.common.base.Strings")
          .log(WARNING, "Exception during lenientFormat for " + objectToString, e);
      return "<" + objectToString + " threw " + e.getClass().getName() + ">";
    }
  }
}
