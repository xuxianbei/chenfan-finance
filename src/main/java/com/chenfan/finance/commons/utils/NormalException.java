package com.chenfan.finance.commons.utils;

/**
 * 自定义异常,实际上是个空的
 *
 * @author SXR
 * @date 2018/7/13
 */
public class NormalException extends Exception {

  public NormalException(String message, Throwable cause) {
    super(message, cause);
  }

  public NormalException(Throwable cause) {
    super(cause);
  }

  protected NormalException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public NormalException(String s) {
    super(s);
  }
}
