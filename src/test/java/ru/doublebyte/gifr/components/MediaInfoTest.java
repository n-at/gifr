package ru.doublebyte.gifr.components;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MediaInfoTest {

    private final MediaInfo mediaInfo = new MediaInfo(null);

    @Test
    void ffprobeInteger() {
        assertNull(mediaInfo.ffprobeInteger(null));
        assertNull(mediaInfo.ffprobeInteger(""));
        assertNull(mediaInfo.ffprobeInteger("N/A"));
        assertNull(mediaInfo.ffprobeInteger("string"));
        assertEquals(42, mediaInfo.ffprobeInteger("42"));
    }

    @Test
    void ffprobeBoolean() {
        assertFalse(mediaInfo.ffprobeBoolean(null));
        assertFalse(mediaInfo.ffprobeBoolean(""));
        assertFalse(mediaInfo.ffprobeBoolean("N/A"));
        assertFalse(mediaInfo.ffprobeBoolean("string"));
        assertFalse(mediaInfo.ffprobeBoolean("0"));
        assertTrue(mediaInfo.ffprobeBoolean("1"));
        assertTrue(mediaInfo.ffprobeBoolean("2"));
    }

    @Test
    void ffprobeDouble() {
        assertNull(mediaInfo.ffprobeDouble(null));
        assertNull(mediaInfo.ffprobeDouble(""));
        assertNull(mediaInfo.ffprobeDouble("N/A"));
        assertNull(mediaInfo.ffprobeDouble("string"));
        assertEquals(1, mediaInfo.ffprobeDouble("1"));
        assertEquals(1.2345, mediaInfo.ffprobeDouble("1.2345"));
    }

    @Test
    void ffprobeFraction() {
        assertNull(mediaInfo.ffprobeFraction(null));
        assertNull(mediaInfo.ffprobeFraction(""));
        assertNull(mediaInfo.ffprobeFraction("N/A"));
        assertNull(mediaInfo.ffprobeFraction("string"));
        assertNull(mediaInfo.ffprobeFraction("1/2/3"));
        assertNull(mediaInfo.ffprobeFraction("1/"));
        assertNull(mediaInfo.ffprobeFraction("/1"));
        assertEquals(42, mediaInfo.ffprobeFraction("42"));
        assertEquals(21, mediaInfo.ffprobeFraction("42/2"));
        assertEquals(1.0/3.0, mediaInfo.ffprobeFraction("1/3"));
    }
}
