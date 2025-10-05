
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedList;

public class Languages {

    private static LinkedList<String> ReadFile( String filePath ) throws Exception{

        LinkedList<String> words = new LinkedList<>();
        BufferedReader br = null;

        try{

            br = new BufferedReader(new FileReader( filePath ));
            String s;

            while ((s = br.readLine()) != null){
                words.add(s);
            }

        } finally {
            if (br != null)
                br.close();
        }

        return words;

    }

    public static HashMap<String, LinkedList<String>> ReadDictionary() throws Exception{

        File folder = new File("./dictionary");
        File[] lang = folder.listFiles();

        if (lang == null)
            throw new EmptyDictionaryException();

        HashMap<String, LinkedList<String>> languages = new HashMap<>();

        for (File file : lang){

            if (file.isFile()){

                LinkedList<String> words = ReadFile( file.getPath() );

                String languageName = file.getName();
                languageName = languageName.substring(0, languageName.indexOf('.'));

                languages.put( languageName, words );

            } else {
                throw new LanguageIsNotAFileException( file.getName() );
            }

        }

        return languages;

    }

    public static WordHBox[] PrepareWords (String selectedLan, HashMap<String, LinkedList<String>> languages, boolean setSelectedWords){

        LinkedList<String> Words = new LinkedList<>();

        LinkedList<String> languageSet = languages.get( selectedLan );

        int size = languageSet.size();

        for (int i = 0; i < 30; i++){

            int index = (int)Math.floor(Math.random() * size);
            String word = languageSet.get(index);

            if (Words.contains( word ))
                i--;
            else
                Words.add( word );

        }

        //set new words or copy and add words to an already existing set:
        if (setSelectedWords)
            Main.setSelectedWords( Words );
        else
            Main.addNextWordSet( Words );

        WordHBox[] wordHBoxes = new WordHBox[30];

        for (int i = 0; i < 30; i++)
            wordHBoxes[i] = new WordHBox( Words.get(i) );

        return wordHBoxes;

    }

}
