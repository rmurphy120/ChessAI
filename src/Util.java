public class Util {
    /**
     * Helper method to convert an x,y coordinate to a bitboard
     *
     * @param x the x position
     * @param y the y position
     * @return a bitboard. 63 bits will be zero and 1 bit will be 1
     */
    public static long coordinateToLongBinary(int x, int y) {
        long out = 1;

        for(int i = 0; i < 63 - (x + 8*y); i++)
            out *= 2;

        return out;
    }

    /**
     * Formatting helper method that converts a bitboard to a binary string with leading 0s up to 64 digits
     *
     * @param l the long number
     * @return a String representing the bitboard with exactly 64 digits
     */
    public static String longToStringBinary(long l) {
        String out = Long.toBinaryString(l);

        while(out.length() < 64)
            out = "0" + out;

        return out;
    }
}
