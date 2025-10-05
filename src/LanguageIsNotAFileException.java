public class LanguageIsNotAFileException extends Exception{
    public LanguageIsNotAFileException(String fileName){
        super("Language " + fileName + " does not appear to be a file.");
    }

}
