package com.fightflow.util;

public final class FilenameUtil {
  private FilenameUtil() {}

  public static String extensionLower(String filename) {
    if (filename == null) return "";
    int idx = filename.lastIndexOf('.');
    if (idx < 0 || idx == filename.length() - 1) return "";
    return filename.substring(idx + 1).toLowerCase();
  }
}

