import utils.fftTransformer.BufferForInt;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class Runner {

    public static void main(String[] args) {

        /*
        * READ REF audio file from defined path and convert it into array of shorts
        * */
        ArrayList<Short> referenceSignalArray = AudioConverter.saveAudioToArrayList("src/main/data/REF.wav");


        /*
        * READ DUT audio file from defined path and convert it into array of shorts
        * */
        ArrayList<Short> dutSignalArray = AudioConverter.saveAudioToArrayList("src/main/data/dataset_task2/DUT.wav");

        /*
        * CREATE results directory
        * */
        File results = new File("src/main/data/results/");
        if(!results.exists()) {
            results.mkdir();
        }
        
       
        /*
        * CREATE fft directory
        * */
        File fft = new File("src/main/data/fft/");
        if(!fft.exists()) {
            fft.mkdir();
        }
        
        
        /*
        * WRITE Synchronized track from to defined file
        * */
        AudioConverter.saveAudioFromArrayListToFile(Synchronizer.synchronize(referenceSignalArray,dutSignalArray),"src/main/data/results/DUTsync.pcm");

        /*
         * READ Synchronized track from file (OPTIONAL) You may use array directly from previous step to increase performance
         * */
         ArrayList<Short> synchronizedDut = AudioConverter.savePCMtoArrayList("src/main/data/results/DUTsync.pcm");


        /*
        * GET FFT of REF signal
        * */
         ArrayList<BufferForInt> referenceSignalFFT =  AudioConverter.transformFFT(AudioConverter.listToArray(referenceSignalArray));

        /*
         * GET FFT of DUT signal
         * */
        ArrayList<BufferForInt> dutSignalFFT =  AudioConverter.transformFFT(AudioConverter.listToArray(synchronizedDut));

        /*
        * Save DUT FFT data to text file
        * */
        AudioConverter.saveFFTtoFile(dutSignalFFT, "src/main/data/fft/dutfft.txt");
        /*
         * Save REF FFT data to text file
         * */
        AudioConverter.saveFFTtoFile(referenceSignalFFT, "src/main/data/fft/reffft.txt");





    }

}
