package se.grenby.kollo.sos.bbb;

/**
 * Created by peteri on 5/30/16.
 */
class SosByteBlockBufferPosition {

    private int position;

    public SosByteBlockBufferPosition(int position) {
        this.position = position;
    }

    public void incByte() {
        position += Byte.BYTES;
    }

    public void incShort() {
        position += Short.BYTES;
    }

    public void incInteger() {
        position += Integer.BYTES;
    }

    public void incLong() {
        position += Long.BYTES;
    }

    public void incFloat() {
        position += Float.BYTES;
    }

    public void incDouble() {
        position += Double.BYTES;
    }

    public void addLength(int length) {
        position += length;
    }

    public int position() {
        return position;
    }

    @Override
    public String toString() {
        return "SosByteBlockBufferPosition{" +
                "position=" + position +
                '}';
    }
}
