import com.alibaba.fastjson.JSON;

import java.util.*;

/**
 *  calculate the probability for each word in code comments
 */
public class ProbabilityCalculator {
    private String datasetPath;
    private String language;
    public ProbabilityCalculator(String datasetPath,String language){
        this.datasetPath = datasetPath;
        this.language = language;
    }

    public void calculate(){
        String trainComment = ReadWriteUtil.readFile(datasetPath+"train\\javadoc.original");
        String testComment = ReadWriteUtil.readFile(datasetPath+"test\\javadoc.original");
        String devComment = ReadWriteUtil.readFile(datasetPath+"dev\\javadoc.original");

        String trainComments[] = trainComment.split("\n");
        String testComments[] = testComment.split("\n");
        String devComments[] = devComment.split("\n");

        Set<String> vocabulary = new HashSet<>();   //vocabulary of the comments in the dataset
        HashMap<String,Integer> counter = new HashMap<>(); //count frequency for each word

        int cnt=0;
        for(String line:trainComments){
            List<String> words = NLPUtil.processComment(line);
            for(String w:words){
                vocabulary.add(w);
                if(counter.containsKey(w)){
                    counter.put(w,counter.get(w)+1);
                }else{
                    counter.put(w,1);
                }
            }
            cnt++;
        }

        for(String line:testComments){
            List<String>words = NLPUtil.processComment(line);
            for(String w:words){
                vocabulary.add(w);
                if(counter.containsKey(w)){
                    counter.put(w,counter.get(w)+1);
                }else{
                    counter.put(w,1);
                }
            }
            cnt++;
        }

        for(String line:devComments){
            List<String>words = NLPUtil.processComment(line);
            for(String w:words){
                vocabulary.add(w);
                if(counter.containsKey(w)){
                    counter.put(w,counter.get(w)+1);
                }else{
                    counter.put(w,1);
                }
            }
            cnt++;
        }

        List<Map.Entry<String,Integer>> sortList = new ArrayList<>(counter.entrySet());
        Collections.sort(sortList, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });

        double sum = 0; // The total frequency of words
        for(Map.Entry<String,Integer>entry:sortList){
            sum+=entry.getValue();
        }

        HashMap<String,Double>probability = new HashMap<>();
        for(Map.Entry<String,Integer>entry:sortList){
            probability.put(entry.getKey(),entry.getValue().doubleValue()/sum);
        }

        ReadWriteUtil.writeFile(language+"Frequency.json",JSON.toJSONString(counter,true));
        ReadWriteUtil.writeFile(language+"Probability.json", JSON.toJSONString(probability, true));
    }

    public static void main(String args[]){
        ProbabilityCalculator javaCalculator = new ProbabilityCalculator(DataSet.javaDataPath,"java");
        ProbabilityCalculator pythonCalculator = new ProbabilityCalculator(DataSet.pythonDataPath,"python");
        javaCalculator.calculate();
        pythonCalculator.calculate();
    }
}
