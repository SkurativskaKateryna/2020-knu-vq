import java.util.ArrayList;


public class Synchronizer {

    /**
     * Calculating correlation between 2 ArrayLists for windowSize amount of elements
     * @param startIndex starting point from which elements of signalToSynchronize array are taken
     * @param shift shift from the beginning of refSignal
     * @param windowSize size of refSignal window to compare with signalToSynchronize
     */
    public static double correlation(ArrayList<Short> referenceSignal, ArrayList<Short> signalToSynchronize,
                                     int startIndex, int shift, int windowSize){

        double res = 0.0;

        for (int i = 0; i < windowSize; i++){
            res += (referenceSignal.get(i + shift) * signalToSynchronize.get(startIndex + i));
        }

        res /= windowSize;

        return res;
    }

    /**
     * Maximum of ArrayList of Doubles function
     */
    public static Double maxvalue(ArrayList<Double> correlations){

        Double x = correlations.get(0);

        for (int i = 1; i < correlations.size(); i++){

            if (correlations.get(i) > x){

                x = correlations.get(i);
            }
        }

        return x;
    }

    /**
     * Function roughly finds the longest mute in refSignal by counting values in array which are close to zero
     */
    public static int longestMuteInRefFinder(ArrayList<Short> referenceSignal){

        int res = 0; //length of the longest mute

        int i = 0;

        while (i < referenceSignal.size()){

            int j = 0;

            while (i + j < referenceSignal.size() && Math.abs(referenceSignal.get(i + j)) <= 500){
                j++;
            }

            if (j > res){
                res = j;
            }

            if (j > 0){
                i += j;
            }
            else{
                i++;
            }

        }

        return res;
    }


    public static short[] synchronize(ArrayList<Short> referenceSignal, ArrayList<Short> signalToSynchronize) {

        // WRITE YOUR REALIZATION HERE:

        ArrayList<Double> correlations = new ArrayList<>(signalToSynchronize.size() - 22050);
        short[] syncSignal = new short[referenceSignal.size()]; // array for synchronized signal

        /*
        First time correlation calculating to find the beginning of speech in signalToSynchronize
         */

        for (int i = 0; i < (signalToSynchronize.size() - 22050); i++){
            double r = correlation(referenceSignal, signalToSynchronize, i, 44100, 22050);
            correlations.add(r);
        }

        int index = correlations.indexOf(maxvalue(correlations)); // place in ArrayList where correlation is max

        /*
        Here are two cases: when the beginning of speech in signalToSynchronize is after the first second
        (therefore we need to shift array to the left), and when the beginning of speech is before the first second
        (therefore we shift array to the right and fill empty space in its beginning using CCF)
         */
        if (index >= 44100){

            for (int i = index; i < signalToSynchronize.size() + 44100; i++){
                syncSignal[i - index] = signalToSynchronize.get(i - 44100);
            }

            /*
            Now we have in syncSignal first part of synchronized signal
            (indexes from [0] to [signalToSynchronize.size() + 44100 - index - 1])
             */

            correlations.clear();

            /*
            Second time calculating correlations to find glue place in signalToSynchronize
             */

            int windowSize = longestMuteInRefFinder(referenceSignal);
            // according to signal view in audacity it should be around 68 000

            for (int i = 0; i < index - 22050; i++){
                double r = correlation(referenceSignal, signalToSynchronize, i,
                        signalToSynchronize.size() + 44100 - index, windowSize);
                correlations.add(r);
            }


            int new_index = correlations.indexOf(maxvalue(correlations));

            /*
            Filling array to the end
             */
            for (int i = 0; i < referenceSignal.size() - signalToSynchronize.size() + index - 44100; i++){
                syncSignal[signalToSynchronize.size() + 44100 - index + i] = signalToSynchronize.get(new_index + i);
            }

        }

        else{

            /*
            Just filling syncSignal array from the index in refSignal where signalToSynchronize starts to the end
             */
            for(int i = 0; i < referenceSignal.size() + index - 44100; i++){
                syncSignal[i + 44100 - index] = signalToSynchronize.get(i);
            }


            correlations.clear();

            /*
            Finding the missing part of signal in the beginning using CCF and fulfilling the syncSignal array
             */
            for (int i = 0; i < signalToSynchronize.size() - 44100 + index; i++){
                double r = correlation(referenceSignal, signalToSynchronize, i, 0, 44100 - index);
                correlations.add(r);
            }

            int new_index = correlations.indexOf(maxvalue(correlations)); // index where the beginning of speech is

            for(int i = 0; i < 44100 - index; i++){
                syncSignal[i] = signalToSynchronize.get(new_index + i);
            }

        }


        return syncSignal;
    }

}
