import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Date;
import java.util.LinkedList;

public class SaveData {

    public static void save(LinkedList<Long> timePerWord, LinkedList<String> selectedWords) throws Exception{

        BufferedWriter bw = null;
        String fileName = getDate() + ".txt";
        File result = new File("./results", fileName);

        try{

            bw = new BufferedWriter(new FileWriter( result ));

            for (int i = 0; i < selectedWords.size(); i++){

                if(timePerWord.size() > i ) {

                    if (timePerWord.get(i) == 0)        //word skipped
                        bw.write(selectedWords.get(i) + " -> 0wpm");
                    else
                        bw.write(selectedWords.get(i) + " -> " + (int) (60000 / (double) timePerWord.get(i)) + "wpm");

                } else {
                    bw.write(selectedWords.get(i) + " -> 0wpm");
                }

                if(i != selectedWords.size() - 1)
                    bw.newLine();

            }

        } finally {
            if (bw != null)
                bw.close();
        }

    }

    private static String getDate(){

        Date d = new Date();
        String date = d.toString();
        String year = date.substring(date.lastIndexOf(' ') + 1);
        date = date.substring(date.indexOf(' ') + 1, date.lastIndexOf(' '));
        date = date.replaceFirst(" ", ".");
        date = year + "." + date;
        date = date.replaceAll(":", "-");
        return date;

    }

}
