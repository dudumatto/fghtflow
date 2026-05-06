package com.fightflow.util;

import java.util.Set;

public final class UploadRules {
  private UploadRules() {}

  public static final long MAX_BYTES_DEFAULT = 10L * 1024L * 1024L;

  public static final Set<String> ALLOWED_MIME_TYPES = Set.of(
      "application/pdf",
      "application/msword",
      "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
  );

  public static final Set<String> ALLOWED_EXTENSIONS = Set.of("pdf", "doc", "docx");
}

