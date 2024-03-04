package comp3111.covid;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Multithreading country loader for speed up the loading
 */
public class CountryLoadThread {
    /**
     * Thread for loading
     */
    public static class LoadThread extends Thread {
        ArrayList<Thread> threads;
        List<String> countryNames;
        ArrayList<Country> countryList;

        /**
         * Constructor
         * @param countryNames list of name for this thread
         */
        LoadThread(List<String> countryNames) {
            this.countryNames = countryNames;
            countryList = new ArrayList<>();
        }

        /**
         * the loop for loading
         */
        @Override
        public void run() {
            for (String countryName : countryNames) {
                try {
                    Country newCountry = new Country(countryName);
                    countryList.add(newCountry);
                } catch (ParseException ignored) {
                }
            }
        }
    }

    ArrayList<Country> countryList;
    List<String> countryNames;
    ArrayList<LoadThread> threads;
    int numOfThread;

    /**
     * Constructor
     * @param countryNames list of name of desired countries
     * @param numOfThread the number of thread for loading (4 is optimal, increasing will not be beneficial)
     */
    CountryLoadThread(List<String> countryNames, int numOfThread) {
        this.countryNames = countryNames;
        countryList = new ArrayList<>();
        this.numOfThread = numOfThread;
        threads = new ArrayList<>();
        int start = 0;
        int end = countryNames.size() / numOfThread;
        for (int i = 0; i < numOfThread; i++) {
            List<String> sublist;
            if (i == numOfThread - 1) {
                sublist = countryNames.subList(start, countryNames.size());
            } else {
                sublist = countryNames.subList(start, end);
            }
            LoadThread newThread = new LoadThread(sublist);
            threads.add(newThread);
            start = end;
            end = (i + 2) * countryNames.size() / numOfThread;

        }
    }

    /**
     * start the thread(s)
     */
    public void start() {
        for (LoadThread thread : threads) {
            thread.start();
        }
    }

    /**
     * wait until all thread(s) ready
     */
    public void join() {
        for (LoadThread thread : threads) {
            try {
                thread.join();
                countryList.addAll(thread.countryList);
            } catch (InterruptedException ignored) {
            }
        }
    }

    /**
     * get the list of countries (the order is not preserved)
     * @return the list of countries
     */
    public List<Country> getCountryList() {
        return countryList;
    }
}
