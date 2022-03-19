import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.*;

/**
 * investigate the effectiveness of existing approaches in supplementary code comment generation
 *
 * First, follow https://github.com/wasiahmad/NeuralCodeSum to train the seq2seq model or transformer model
 * save the result in seq2seqResult.json or transformerResult.json
 *
 * Then used MESIA to investigate the result.
 */
public class RQ2 {

    public static void main(String args[]){
        String result = ReadWriteUtil.readFile(DataSet.projectPath+"seq2seqResult.json");
        //String result = ReadWriteUtil.readFile(DataSet.projectPath+"transformerResult.json");

        String[] lines = result.split("\n");
        HashMap<String, Double> Bleu = new HashMap<>();
        HashMap<String, Double> Rouge_l = new HashMap<>();
        HashMap<String, Double> MESIA = new HashMap<>();
        HashMap<String,Double> generatedMESIA = new HashMap<>();

        String testCode = ReadWriteUtil.readFile(DataSet.javaDataPath+"test\\code.original");
        String testCodes[] = testCode.split("\n");
        MESIAcalculator calculator = new MESIAcalculator("javaProbability.json");

        int linenum = 0;
        for (String r:lines) {
            String code = testCodes[linenum];
            linenum++;
            JSONObject line = JSON.parseObject(r);
            String prediction = line.getString("predictions").substring(2, line.getString("predictions").length() - 2);
            String reference = line.getString("references").substring(2, line.getString("references").length() - 2);
            String bleu = line.getString("bleu");
            String rouge_l = line.getString("rouge_l");
            String id = line.getString("id");
            Bleu.put(id, Double.valueOf(bleu));
            Rouge_l.put(id, Double.valueOf(rouge_l));

            List<String> codeFeatures = new ArrayList<>();
            codeFeatures.addAll(NLPUtil.camelSplit(NLPUtil.snakeSplit(NLPUtil.getJavaMethodName(code))));

            double mesia = calculator.getMESIA(reference,codeFeatures);
            double generatedMesia = calculator.getMESIA(prediction,codeFeatures);
            MESIA.put(id, mesia);
            generatedMESIA.put(id,generatedMesia);
        }

        // rank the test set according to MESIA
        List<Map.Entry<String, Double>> sortMESIA = new ArrayList<Map.Entry<String, Double>>(MESIA.entrySet());
        Collections.sort(sortMESIA, new Comparator<Map.Entry<String, Double>>() {
            @Override
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });

        //divide the test set into 10 parts and investigate the result
        int size = sortMESIA.size()/10;

        double bleu[] = new double[10];
        double rough_l[] = new double[10];
        double generated_mesia[] = new double[10];

        for(int i=0;i<10;++i){
            bleu[i]=0;
            rough_l[i]=0;
            generated_mesia[i]=0;
        }

        for(int i=0;i<8700;++i){
            bleu[i/size]+=Bleu.get(sortMESIA.get(i).getKey());
            rough_l[i/size]+=Rouge_l.get(sortMESIA.get(i).getKey());
            generated_mesia[i/size]+=generatedMESIA.get(sortMESIA.get(i).getKey());
        }

        for(int i=0;i<10;++i){
            bleu[i]/=size;
            rough_l[i]/=size;
            generated_mesia[i]/=size;
        }

        System.out.println("BLEU:");
        for(int i=0;i<10;++i){
            System.out.println(bleu[i]);
        }

        System.out.println("ROUGH_L:");
        for(int i=0;i<10;++i){
            System.out.println(rough_l[i]);
        }

        System.out.println("MESIA:");
        for(int i=0;i<10;++i){
            System.out.println(generated_mesia[i]);
        }
    }
}
