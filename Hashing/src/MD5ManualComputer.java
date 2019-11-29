import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

class MD5ManualComputer {

    private static final int INIT_A = 0x67452301;
    private static final int INIT_B = (int) 0xEFCDAB89L;
    private static final int INIT_C = (int) 0x98BADCFEL;
    private static final int INIT_D = 0x10325476;


    private static final int SINGLE_WORD_SIZE = 4;
    private static final int BYTES_COUNT_IN_SINGLE_BLOCK = 64;

    //Constant S values
    private static final int[] SHIFTING_CONSTANT_VALUES = {
            7, 12, 17, 22, 7, 12, 17, 22, 7, 12, 17, 22, 7, 12, 17, 22,
            5, 9, 14, 20, 5, 9, 14, 20, 5, 9, 14, 20, 5, 9, 14, 20,
            4, 11, 16, 23, 4, 11, 16, 23, 4, 11, 16, 23, 4, 11, 16, 23,
            6, 10, 15, 21, 6, 10, 15, 21, 6, 10, 15, 21, 6, 10, 15, 21
    };

    private static final int[] TABLE_T = new int[64];

    //Calculating T constant values
    static {
        for (int i = 0; i < 64; i++) {
            TABLE_T[i] = (int) (long) ((1L << 32) * Math.abs(Math.sin(i + 1)));
        }
    }

    static String computeMD5(String message) {
        byte[] bytesOfMessage = message.getBytes(StandardCharsets.UTF_8);
        int messageLenBytes = bytesOfMessage.length;
        int numBlocks = ((messageLenBytes + 8) / BYTES_COUNT_IN_SINGLE_BLOCK) + 1;
        int totalLen = numBlocks * BYTES_COUNT_IN_SINGLE_BLOCK; //Each Block has 64 byte (512 bits)
        byte[] paddingBytes = new byte[totalLen - messageLenBytes];
        paddingBytes[0] = (byte) 0x80;

        long messageLenBits = (long) messageLenBytes * 8;
        for (int i = 0; i < 8; i++) {
            paddingBytes[paddingBytes.length - 8 + i] = (byte) messageLenBits;
            messageLenBits /= 256;
        }

        byte[] totalMessageBytes = concatArrays(bytesOfMessage, paddingBytes);

        int a = INIT_A;
        int b = INIT_B;
        int c = INIT_C;
        int d = INIT_D;

        //Block Words (16 * 32-bit words = 512 -bits in a block-)
        int[] buffer = new int[16];

        //MD5 works for each block in a msg
        for (int i = 0; i < numBlocks; i++) {
            int index = i * BYTES_COUNT_IN_SINGLE_BLOCK; // index of the first byte in the current block relative to the whole msg = blockIndex * 64

            //Getting the 16-words values of the current block by wrapping each 4 bytes of the block as an int-word in little endian format
            for (int byteInBlockIndex = 0; byteInBlockIndex < BYTES_COUNT_IN_SINGLE_BLOCK; byteInBlockIndex += SINGLE_WORD_SIZE, index += SINGLE_WORD_SIZE) {
                byte[] word = new byte[SINGLE_WORD_SIZE];
                System.arraycopy(totalMessageBytes, index, word, 0, word.length);
                ByteBuffer wrapped = ByteBuffer.wrap(word).order(ByteOrder.LITTLE_ENDIAN);
                buffer[byteInBlockIndex / 4] = wrapped.getInt();
            }

            int originalA = a;
            int originalB = b;
            int originalC = c;
            int originalD = d;

            for (int byteInBlockIndex = 0; byteInBlockIndex < BYTES_COUNT_IN_SINGLE_BLOCK; byteInBlockIndex++) {

                int roundIndex = byteInBlockIndex / 16; // To Detect in which round we are currently
                int firstOrderFunction = 0;
                int bufferIndex = byteInBlockIndex;

                switch (roundIndex) {
                    case 0:
                        firstOrderFunction = (b & c) | (~b & d);
                        break;

                    case 1:
                        firstOrderFunction = (b & d) | (c & ~d);
                        bufferIndex = (bufferIndex * 5 + 1) & 0x0F;
                        break;

                    case 2:
                        firstOrderFunction = b ^ c ^ d;
                        bufferIndex = (bufferIndex * 3 + 5) & 0x0F;
                        break;

                    case 3:
                        firstOrderFunction = c ^ (b | ~d);
                        bufferIndex = (bufferIndex * 7) & 0x0F;
                        break;
                }

                int secondOrderFunction = b + Integer.rotateLeft(a + firstOrderFunction + buffer[bufferIndex] + TABLE_T[byteInBlockIndex], SHIFTING_CONSTANT_VALUES[byteInBlockIndex]);
                a = d;
                d = c;
                c = b;
                b = secondOrderFunction;
            }

            a += originalA;
            b += originalB;
            c += originalC;
            d += originalD;
        }


        byte[] md5 = new byte[16];
        int count = 0;
        for (int i = 0; i < 4; i++) {
            int n;
            switch (i) {
                case 0:
                    n = a;
                    break;
                case 1:
                    n = b;
                    break;
                case 2:
                    n = c;
                    break;
                default:
                    n = d;
                    break;
            }
            for (int j = 0; j < 4; j++) {
                md5[count++] = (byte) n;
                n >>>= 8;
            }
        }
        return toHexString(md5);
    }

    static String toHexString(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (byte b1 : b) {
            sb.append(String.format("%02X", b1));
        }
        return sb.toString();
    }

    private static byte[] concatArrays(byte[] arr1, byte[] arr2) {
        byte[] resultArray = new byte[arr1.length + arr2.length];
        int globalIndex = 0;
        for (byte b : arr1) {
            resultArray[globalIndex] = b;
            globalIndex++;
        }
        for (byte b : arr2) {
            resultArray[globalIndex] = b;
            globalIndex++;
        }
        return resultArray;
    }
}