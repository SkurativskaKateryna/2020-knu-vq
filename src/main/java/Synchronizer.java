import java.util.ArrayList;


public class Synchronizer {

    /**
     * Counting a correlation between 2 ArrayLists for 22050 elements (0.5 sec window)
     * @param startIndex
     * @param shift means shift from the beginning of refSignal (44100 recommended)
     */
    public static double correlation(ArrayList<Short> referenceSignal, ArrayList<Short> signalToSynchronize,
                                     int startIndex, int shift){

        double res = 0.0;

        for (int i = 0; i < 22050; i++){
            res += (referenceSignal.get(i + shift) * signalToSynchronize.get(startIndex + i));
        }

        res /= 22050;

        return res;
    }

    /**
     * Maximum of ArrayList function
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


    public static short[] synchronize(ArrayList<Short> referenceSignal, ArrayList<Short> signalToSynchronize) {

    // WRITE YOUR REALIZATION HERE:


        ArrayList<Double> correlations = new ArrayList<Double>(signalToSynchronize.size() - 22050);
        short[] syncSignal = new short[signalToSynchronize.size()]; // array for synchronized signal

        /*
        First time correlation counting to find the beginning of text in signalToSynchronize

         */
        for (int i = 0; i < (signalToSynchronize.size() - 22050); i++){
            double r = correlation(referenceSignal, signalToSynchronize, i, 44100);
            correlations.add(r);
        }

        int index = correlations.indexOf(maxvalue(correlations));

        for (int i = index; i < signalToSynchronize.size() + 44100; i++){
            syncSignal[i - index] = signalToSynchronize.get(i - 44100);
        }


        correlations.clear();

        /*
        Second time counting correlations with such shift to find glue place in signalToSynchronize
         */
        for (int i = 0; i < index - 22050; i++){
            double r = correlation(referenceSignal, signalToSynchronize, i,
                    signalToSynchronize.size() + 44100 - index);
            correlations.add(r);
        }


        int new_index = correlations.indexOf(maxvalue(correlations));

        /*
        Filling array to the end
         */
        for (int i = 0; i < index - 44100; i++){
            syncSignal[signalToSynchronize.size() + 44100 - index + i] = signalToSynchronize.get(new_index + i);
        }


        return syncSignal;
    }

}
