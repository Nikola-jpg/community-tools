package com.community.tools.util;

public class MessageUtils {

  /**
   * Get emoji by event type.
   * @param type event type name.
   * @return emoji string.
   */
  public static String emojiGen(String type) {
    switch (type) {
      case "COMMENT":
        return ":loudspeaker:";
      case "COMMIT":
        return ":rolled_up_newspaper:";
      case "PULL_REQUEST_CLOSED":
        return ":moneybag:";
      case "PULL_REQUEST_CREATED":
        return ":mailbox_with_mail:";
      default:
        return "";
    }
  }

  public static String getTypeTitleBold(String typeTitle) {
    return "*" + typeTitle + "*";
  }
}
