import edu.stanford.nlp.simple.Sentence;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class to process the code and comments
 */
public class NLPUtil {
    /**
     * some of the punctuations are omitted in implementation
     * This is manually added
     */
    public static String stops[] = {
            "{", "|", "}", "~", "!", "\"", "#", "$", "'", "(", ")", ",", "-",
            ".", "_", "__", "___", "______", "`", "?", "[", "\\", "]", "_", ":", ";",};

    public static List<String> stopWords = new ArrayList<>();

    public static List<String>getStopWords(){
        if(stopWords.isEmpty()){
            for(String w:stops){
                stopWords.add(w);
            }
        }
        return stopWords;
    }

    /**
     * split the comment and lemmatize the word to a normal form
     */
    public static List<String> processComment(String comment){
        List<String>result = new ArrayList<>();
        String []words = comment.split(" ");
        for(String w:words){
            if(getStopWords().contains(w))continue;
            if(w.isEmpty())continue;
            List<String>tokens = snakeSplit(w);
            for(String s:tokens){
                try{
                    String tokenLemma = new Sentence(s.toLowerCase()).lemma(0);
                    result.add(tokenLemma);
                }catch (Exception e){
                }
            }
        }
        return result;
    }

    public static List<String> Stem(List<String>words){
        List<String>result = new ArrayList<>();
        for(String w:words){
            String tokenLemma = new Sentence(w.toLowerCase()).lemma(0);
            result.add(tokenLemma);
        }
        return result;
    }


    public static List<String> snakeSplit(String name){
        List<String>result = new ArrayList<>();
        String[]names = name.split("::");
        for(String n:names){
            String[]S = n.split("_");
            for(String s:S){
                if(!s.trim().equals("")){
                    result.add(s.trim());
                }
            }
        }
        return result;
    }

    public static List<String>camelSplit(List<String>names){
        List<String>result = new ArrayList<>();
        for(String name:names){
            int prev=0;
            for(int i=0;i<name.length();++i){
                if(i < name.length()-1 && Character.isLowerCase(name.charAt(i)) && Character.isUpperCase(name.charAt(i + 1))) {
                    result.add(name.substring(prev,i+1).toLowerCase());
                    prev=i+1;
                }
            }
            result.add(name.substring(prev).toLowerCase());
        }
        return result;
    }

    /**
     * In code tokens of java data, method name is followed by the first “(”.
     * @param code
     * @return method name
     */
    public static String getJavaMethodName(String code){
        List<String>codeTokens=new ArrayList<>();
        String [] codes = code.split(" ");
        for(int i=0;i<codes.length;++i){
            codeTokens.add(codes[i]);
        }
        return codeTokens.get(codeTokens.indexOf("(")-1);
    }

    /**
     * In code tokens of python data, method name is after the key word "def"
     * @param code
     * @return method name
     */
    public static String getPythonMethodName(String code){
        List<String>codeTokens=new ArrayList<>();
        String [] codes = code.split(" ");
        for(int i=0;i<codes.length;++i){
            codeTokens.add(codes[i]);
        }
        return codeTokens.get(codeTokens.indexOf("def")+1);
    }

    public static String getMethodName(String code,String language){
        if(language.equals("java")) return getJavaMethodName(code);
        if(language.equals("python")) return getPythonMethodName(code);
        return null;
    }
}
