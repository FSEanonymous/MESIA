import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RQ1 {

    /**
     * most of MESIA scores are <10 , so we divide it into 10 intervals
     */
    public static int getIntervalId(double mesia){
        if(mesia<1)return 0;
        else if(mesia<2)return 1;
        else if(mesia<3)return 2;
        else if(mesia<4)return 3;
        else if(mesia<5)return 4;
        else if(mesia<6)return 5;
        else if(mesia<7)return 6;
        else if(mesia<8)return 7;
        else if(mesia<9)return 8;
        else if(mesia<10)return 9;
        else return 10;
    }

    /**
     *  calculate the MESIA for the dataset
     */
    public static void calculateMESIA(String language){
        String datasetPath = DataSet.getDataSetPath(language);
        String trainComment = ReadWriteUtil.readFile(datasetPath+"train\\javadoc.original");
        String testComment = ReadWriteUtil.readFile(datasetPath+"test\\javadoc.original");
        String devComment = ReadWriteUtil.readFile(datasetPath+"dev\\javadoc.original");
        String trainCode = ReadWriteUtil.readFile(datasetPath+"train\\code.original");
        String testCode = ReadWriteUtil.readFile(datasetPath+"test\\code.original");
        String devCode = ReadWriteUtil.readFile(datasetPath+"dev\\code.original");

        String trainComments[] = trainComment.split("\n");
        String testComments[] = testComment.split("\n");
        String devComments[] = devComment.split("\n");
        String trainCodes[] = trainCode.split("\n");
        String testCodes[] = testCode.split("\n");
        String devCodes[] = devCode.split("\n");

        MESIAcalculator calculator = new MESIAcalculator(language+"Probability.json");
        HashMap<String,Double>CodeComment2MESIA = new HashMap<>();
        StringBuilder info = new StringBuilder("");

        System.out.println("start calculating MESIA......");

        int linenum = 0;
        for (String comment : trainComments) {
            String code = trainCodes[linenum];
            linenum++;
            List<String> codeFeatures = new ArrayList<>();
            codeFeatures.addAll(NLPUtil.camelSplit(NLPUtil.snakeSplit(NLPUtil.getMethodName(code,language))));
            double MESIA = calculator.getMESIA(comment,codeFeatures);
            System.out.println(MESIA);
            CodeComment2MESIA.put(code + "\n" + comment,MESIA);
            info.append(code + "\n" + comment+"\n"+MESIA+"\n");
        }

        linenum = 0;
        for (String comment : devComments) {
            String code = devCodes[linenum];
            linenum++;
            List<String> codeFeatures = new ArrayList<>();
            codeFeatures.addAll(NLPUtil.camelSplit(NLPUtil.snakeSplit(NLPUtil.getMethodName(code,language))));
            double MESIA = calculator.getMESIA(comment,codeFeatures);
            System.out.println(MESIA);
            CodeComment2MESIA.put(code + "\n" + comment,MESIA);
            info.append(code + "\n" + comment+"\n"+MESIA+"\n");
        }

        linenum = 0;
        for (String comment : testComments) {
            String code = testCodes[linenum];
            linenum++;
            List<String> codeFeatures = new ArrayList<>();
            codeFeatures.addAll(NLPUtil.camelSplit(NLPUtil.snakeSplit(NLPUtil.getMethodName(code,language))));
            double MESIA = calculator.getMESIA(comment,codeFeatures);
            System.out.println(MESIA);
            CodeComment2MESIA.put(code + "\n" + comment,MESIA);
            info.append(code + "\n" + comment+"\n"+MESIA+"\n");
        }


        int []MESIAresult = new int[11];
        for(int i=0;i<=10;++i) MESIAresult[i]=0;

        for(Double mesia:CodeComment2MESIA.values()){
            MESIAresult[getIntervalId(mesia)]++;
        }

        for(int i=0;i<=10;++i){
            if(i<10)
                System.out.println("MESIA("+i+"-"+(i+1)+"):"+MESIAresult[i]);
            else
                System.out.println("MESIA(>=10):"+MESIAresult[i]);
        }

        ReadWriteUtil.writeFile(language+"MESIA_Info",info.toString());

        System.out.println("finish calculating MESIA......");
    }

    public static void main(String args[]) {
        calculateMESIA("java");
    }
}