package avtocode;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Parser {

    private static final String COMMA_DELIMITER = ",";

    public static void main(String[] a) throws IOException {
        long start = System.currentTimeMillis();
        int lineCount = 0;
        String fileName = "../sourceFiles/subset-20190226-structure-20170202.csv";
//        String fileName = "../sourceFiles/data-20190226-structure-20170202.csv";
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            int lineNumber = 0;
            while ((line = br.readLine()) != null) {
                lineNumber++;
                String fullname = line.substring(0,line.indexOf(COMMA_DELIMITER));
                fullname = trimQ(fullname);
                parse (fullname);
                lineCount++;
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("lineCount="+lineCount+", firsts="+firsts.size());
        System.out.println("time="+(end-start)+" ms");

        Collection<Counter> countersC = firsts.values();
        Counter[] counters = countersC.toArray(new Counter[countersC.size()]);
        Arrays.sort(counters, (Counter o1, Counter o2) ->o1.counter>o2.counter?-1:o1.counter<o2.counter?1:0);
        int imortant=0;
        for (Counter cnt: counters) {
            if (cnt.counter==1) {
                break;
            }
            imortant++;
            System.out.println(cnt.word+" "+cnt.counter);
        }
        System.out.println("imortant="+imortant);
        System.out.println("ooos="+ooos);
    }

    @NotNull
    private static String trimQ(String fullname) {
        int length = fullname.length();
        int first=0;
        int last=length-1;
        while (last>first) {
            if (fullname.charAt(first)!='"' || fullname.charAt(last)!='"') {
                break;
            }
            first++; last--;
        }
        if (first == 0) {
            return fullname;
        }
        return fullname.substring(first, last);
    }

    static HashMap<String, Counter> firsts = new HashMap<>();

    static void parse(String fullname) {
        String[] words = fullname.split(" ");
        firstsAdd(words[0]);
    }

    static int ooos=0;

    private static void firstsAdd(String word) {
        if ("\"\"ООО".equals(word)) {
            ooos++;
        }
        Counter counter = firsts.get(word);
        if (counter == null) {
            counter = new Counter(word);
            firsts.put(word, counter);
        }
        counter.counter++;
    }

    static class Counter {
        final String word;
        int counter=0;

        Counter(String word) {
            this.word = word;
        }
    }
}
