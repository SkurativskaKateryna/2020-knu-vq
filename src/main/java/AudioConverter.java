import utils.DataProvider;
import utils.fftTransformer.BufferForInt;
import utils.fftTransformer.BufferForShort;
import utils.fftTransformer.RealDoubleFFT;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;




public class AudioConverter {

    private static RealDoubleFFT transformer;
    private static int fftLength = 22050;


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


    /**
    * Convert array of shorts to ArrayList of Shorts
    * */
    public static ArrayList<Short> arrayToList(short[] data) {
        ArrayList<Short> list = new ArrayList<>();

        for (short s : data) {
            list.add(s);
        }

        return list;

    }


    /**
    * Convert ArrayList of Short to array of short
    * */
    public static short[] listToArray(ArrayList<Short> data) {
        short[] array = new short[data.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = data.get(i);
        }
        return array;
    }


    /**
    * Fast Fourier Transformer.
     *
     * Convert signal to its spectrum representation
    * */
    public static ArrayList<BufferForInt> transformFFT(short[] sData) {


        //convert all data to list of Buffers with 22050 numbers.

        ArrayList<BufferForShort> buffersToTransform = new ArrayList<>();

        short [] part = new short[fftLength];
        for(int i = 0, j=0; i< sData.length; i++, j++){
           if(j>=fftLength){
               j=0;
               buffersToTransform.add(new BufferForShort(part));
               part = new short[fftLength];
           }

            part[j] = sData[i];
        }


        ArrayList<BufferForInt> bufferForInts = new ArrayList<>();


        transformer = new RealDoubleFFT(fftLength);

        for(BufferForShort shortBuffer : buffersToTransform){
            double[] toTransform = new double[shortBuffer.getValues().length];

            for (int i = 0; i < shortBuffer.getValues().length; i++) {
                toTransform[i] = (double) shortBuffer.getValues()[i] / 32768.0; // signed 16 bit
            }
            transformer.ft(toTransform);

            int[] ints = new int[toTransform.length];

            for(int i =0; i<toTransform.length;i++){
                ints[i] = (int) Math.abs(toTransform[i]*10);
            }

            bufferForInts.add(new BufferForInt((ints)));
        }
        return bufferForInts;
    }


    /**
     * Read PCM from file and convert it to ArrayList of shorts
     *
     * Convert signal to its spectrum representation
     * */
    public static ArrayList<Short> savePCMtoArrayList(String path){

        ArrayList<Short> result = new ArrayList<>();


        try {
            InputStream in = new FileInputStream(path);

            byte[] audioBytes = new byte[1024 *2];
            short[] shorts;
            while ((in.read(audioBytes)) != -1) {

                shorts = byte2Short(audioBytes);
                for (short s : shorts) {
                    result.add(s);
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }


    /**
    *  Write data stored in BufferForInt to file
    * */
    public static void saveFFTtoFile(ArrayList<BufferForInt> data, String path){

        try( BufferedWriter br = new BufferedWriter( new OutputStreamWriter(new FileOutputStream(path, true), StandardCharsets.UTF_8))) {

            for(BufferForInt d : data){
                for(int i =0; i< d.getValues().length; i++){

                    br.write(d.getValues()[i] + " ");
                }
                br.write("\n");
            }
            br.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
