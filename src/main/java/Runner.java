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
        ArrayList<Short> dutSignalArray = AudioConverter.saveAudioToArrayList("src/main/data/DUT.wav");


        /*
        * WRITE Synchronized track from to defined file
        * */
        AudioConverter.saveAudioFromArrayListToFile(Synchronizer.synchronize(referenceSignalArray,dutSignalArray),"src/main/data/DUTsync.pcm");

    }

}
