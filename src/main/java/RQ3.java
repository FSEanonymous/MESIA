import java.util.*;

/**
 * First, rank the training set according to MESIA and divide it into 10 parts
 * Second, construct 3 new training set: L-training(1-8), M-training(2-9),H-training(3-10)
 * Third, used these three new training set to retrain the models according to: https://github.com/wasiahmad/NeuralCodeSum
 * Finally, investigate the result as RQ2
 * The following code are used to devide the training set and construct new training sets
 */
public class RQ3 {
    public static void main(String args[]){
        String trainComment = ReadWriteUtil.readFile(DataSet.javaDataPath+"train\\javadoc.original");
        String trainCode = ReadWriteUtil.readFile(DataSet.javaDataPath+"train\\code.original");
        String trainSubtoken = ReadWriteUtil.readFile(DataSet.javaDataPath+"train\\code.original_subtoken");
        String trainComments[] = trainComment.split("\n");
        String trainCodes[] = trainCode.split("\n");
        String trainCodeSubtokens[] = trainSubtoken.split("\n");

        MESIAcalculator calculator = new MESIAcalculator("javaProbability.json");
        HashMap<Integer,Double> id2MESIA = new HashMap<>();

        int linenum = 0;
        for(String comment:trainComments){
            String code = trainCodes[linenum];
            List<String> codeFeatures = new ArrayList<>();
            codeFeatures.addAll(NLPUtil.camelSplit(NLPUtil.snakeSplit(NLPUtil.getJavaMethodName(code))));
            double MESIA = calculator.getMESIA(comment,codeFeatures);
            id2MESIA.put(linenum,MESIA);
            linenum++;
        }

        List<Map.Entry<Integer, Double>> sortId2MESIA = new ArrayList<Map.Entry<Integer, Double>>(id2MESIA.entrySet());
        Collections.sort(sortId2MESIA, new Comparator<Map.Entry<Integer, Double>>() {
            @Override
            public int compare(Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });

        int batch = sortId2MESIA.size()/10;

        StringBuilder trainCode1 = new StringBuilder("");
        StringBuilder trainSubtoken1 = new StringBuilder("");
        StringBuilder trainComment1 = new StringBuilder("");

        StringBuilder trainCode2 = new StringBuilder("");
        StringBuilder trainSubtoken2 = new StringBuilder("");
        StringBuilder trainComment2 = new StringBuilder("");

        StringBuilder trainCode3 = new StringBuilder("");
        StringBuilder trainSubtoken3 = new StringBuilder("");
        StringBuilder trainComment3 = new StringBuilder("");


        for(int i=0;i<batch*8;++i){
            trainCode1.append(trainCodes[sortId2MESIA.get(i).getKey()]);
            trainCode1.append("\n");
            trainSubtoken1.append(trainCodeSubtokens[sortId2MESIA.get(i).getKey()]);
            trainSubtoken1.append("\n");
            trainComment1.append(trainComments[sortId2MESIA.get(i).getKey()]);
            trainComment1.append("\n");
        }

        for(int i=2*batch;i<batch*9;++i){
            trainCode2.append(trainCodes[sortId2MESIA.get(i).getKey()]);
            trainCode2.append("\n");
            trainSubtoken2.append(trainCodeSubtokens[sortId2MESIA.get(i).getKey()]);
            trainSubtoken2.append("\n");
            trainComment2.append(trainComments[sortId2MESIA.get(i).getKey()]);
            trainComment2.append("\n");
        }

        for(int i=batch*3;i<batch*10;++i){
            trainCode3.append(trainCodes[sortId2MESIA.get(i).getKey()]);
            trainCode3.append("\n");
            trainSubtoken3.append(trainCodeSubtokens[sortId2MESIA.get(i).getKey()]);
            trainSubtoken3.append("\n");
            trainComment3.append(trainComments[sortId2MESIA.get(i).getKey()]);
            trainComment3.append("\n");
        }

        ReadWriteUtil.writeFile(DataSet.projectPath+"L-training\\train\\code.original",trainCode1.toString());
        ReadWriteUtil.writeFile(DataSet.projectPath+"L-training\\train\\code.original_subtoken",trainSubtoken1.toString());
        ReadWriteUtil.writeFile(DataSet.projectPath+"L-training\\train\\javadoc.original",trainComment1.toString());
        ReadWriteUtil.writeFile(DataSet.projectPath+"M-training\\train\\code.original",trainCode2.toString());
        ReadWriteUtil.writeFile(DataSet.projectPath+"M-training\\train\\code.original_subtoken",trainSubtoken2.toString());
        ReadWriteUtil.writeFile(DataSet.projectPath+"M-training\\train\\javadoc.original",trainComment2.toString());
        ReadWriteUtil.writeFile(DataSet.projectPath+"H-training\\train\\code.original",trainCode3.toString());
        ReadWriteUtil.writeFile(DataSet.projectPath+"H-training\\train\\code.original_subtoken",trainSubtoken3.toString());
        ReadWriteUtil.writeFile(DataSet.projectPath+"H-training\\train\\javadoc.original",trainComment3.toString());
    }
}
