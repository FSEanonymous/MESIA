import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.util.HashMap;
import java.util.List;

/**
 * Calculate the MESIA metric
 *
 * Notice: the MESIA metric is concerned with three factors:
 * (1) the probability of each word to appear in code comments
 *
 * (2) the length of the code comment
 *
 * (3) code features we considered
 */
public class MESIAcalculator {
    private String probabilityPath; // the path where the probability is saved
    private String probabilityResult;
    private HashMap<String,Double>pMap; //the mesia metric need a probability of each word appearing in code comments.

    public MESIAcalculator(String probabilityPath){
        this.probabilityPath = DataSet.projectPath+probabilityPath;
        this.probabilityResult = ReadWriteUtil.readFile(this.probabilityPath);
        this.pMap = JSON.parseObject(this.probabilityResult, new TypeReference<HashMap<String, Double>>(){});
    }

    /**
     *  MESIA = (sigma -log(p(w|code,W)))/len
     * @param comment the given comment
     * @param codeTokens code features considered
     * @return MESIA result
     */
    public double getMESIA(String comment, List<String> codeTokens){
        if(comment.trim().equals(""))return 0;
        List<String>commentTokens = NLPUtil.processComment(comment);
        List<String>stemCode = NLPUtil.Stem(codeTokens);
        if(commentTokens.size()==0)return 0;
        double MESIA = 0;
        for(String w:commentTokens){
            if(stemCode.contains(w))continue;
            if(!pMap.containsKey(w))continue;
            MESIA-=Math.log(pMap.get(w));
        }
        return MESIA/commentTokens.size();
    }
}
