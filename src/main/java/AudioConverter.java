import utils.DataProvider;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;


public class AudioConverter {

    /**
     * This method read data from file placed in selected path using build in AudioInputStream class.
     * This class automatically remove headed from WAV file, so you get only data presented as array of bytes.
     * We also use byte to short converter to get array of shorts.
     */
    public static ArrayList<Short> saveAudioToArrayList(String source) {

        ArrayList<Short> buffers = new ArrayList<>();

        try {
            File audioFile = new File(source);

            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);

            int bytesPerFrame = audioInputStream.getFormat().getFrameSize();

            if (bytesPerFrame == AudioSystem.NOT_SPECIFIED) {
                bytesPerFrame = 1;
            }

            int numBytes = 1024 * bytesPerFrame;

            DataProvider.getInstance().saveData("numOfBytes", numBytes);

            byte[] audioBytes = new byte[numBytes];
            short[] shorts;
            while ((audioInputStream.read(audioBytes)) != -1) {

                shorts = byte2Short(audioBytes);
                for (short s : shorts) {
                    buffers.add(s);
                }

            }

        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }

        return buffers;
    }

    /**
     * This method used for saving array of shorts to output .pcm file.
     * Array of shorts converting to array of bytes and record to file, defined in method parameter.
     */
    public static void saveAudioFromArrayListToFile(short[] list, String path) {


        try (FileOutputStream fileOutputStream = new FileOutputStream(path)) {


            try {
                fileOutputStream.write(short2byte(list));
                fileOutputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Byte to short converter
     */
    private static short[] byte2Short(byte[] bytes) {

        short[] shorts = new short[bytes.length / 2];

        ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);

        return shorts;

    }

    /**
     * Short to byte converter
     */
    private static byte[] short2byte(short[] sData) {
        int shortArrSize = sData.length;

        byte[] bytes = new byte[shortArrSize * 2];

        for (int i = 0; i < shortArrSize; i++) {
            bytes[i * 2] = (byte) (sData[i] & 0x00FF);

            bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);

            sData[i] = 0;
        }
        return bytes;
    }

}
