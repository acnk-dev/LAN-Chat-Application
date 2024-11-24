package constants;

public enum KeyConstants {
    ACTION,
    STATUS,
    USER_ID,

    USERNAME,
    NAME,
    EMAIL,
    PASSWORD,
    PROFILE_PIC,

    // FETCH_ALL_USERS - parallel arrays, since this hand-rolled JSON
    // parser has no clean way to serialize an array of objects
    USER_IDS,
    USER_NAMES,

    // Availability status
    IS_ONLINE,
    ONLINE_USERS,

    // Chat messaging + e2e encryption
    RECEIVER_ID,
    SENDER_ID,
    MESSAGE,
    METADATA,
    KEY,
    PUBLIC_KEY
}
