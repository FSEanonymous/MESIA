import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.eclipse.jdt.core.dom.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Consider different code features and calculate different variants of MESIA
 */
public class RQ4 {
    public static String methodName = "";
    public static List<String> invokedMethodNames = new ArrayList<>();
    public static List<String> variableNames = new ArrayList<>();

    /**
     * parse the code snippet and get its method name, invoked-method names, and variable names
     * @param codeSnippet the content of the code snippet, here is a java method
     * @throws IOException
     */
    public static void codeParse(String codeSnippet) throws IOException {
        ASTParser parsert = ASTParser.newParser(AST.JLS8);
        parsert.setSource(codeSnippet.toCharArray());
        parsert.setKind(ASTParser.K_COMPILATION_UNIT);
        CompilationUnit unit = (CompilationUnit) parsert.createAST(null);
        unit.accept(new ASTVisitor() {
            public boolean visit(VariableDeclarationFragment node) {
                SimpleName name = node.getName();
                variableNames.add(name.toString());
                return false;
            }
            public boolean visit(MethodDeclaration node) {
                String funcName = node.getName().toString();
                methodName = funcName;
                return true;
            }
            public boolean visit(MethodInvocation inv) {
                String funcName = inv.getName().toString();
                invokedMethodNames.add(funcName);
                return true;
            }
        });
    }

    public static void main(String args[])throws Exception{
        String result = ReadWriteUtil.readFile(DataSet.projectPath+"seq2seqResult.json");
        //String result = ReadWriteUtil.readFile(DataSet.projectPath+"transformerResult.json");
        String[] lines = result.split("\n");

        String codeString = ReadWriteUtil.readFile(DataSet.javaDataPath+"test\\code.original");
        String[] codelines = codeString.split("\n");

        HashMap<String,Double> MESIA = new HashMap<>();
        HashMap<String,Double> MESIA_invocation = new HashMap<>();
        HashMap<String,Double> MESIA_variable = new HashMap<>();
        HashMap<String,Double> MESIA_code = new HashMap<>();

        List<Double> MESIA_result = new ArrayList<>();
        List<Double> MESIA_invocation_result = new ArrayList<>();
        List<Double> MESIA_variable_result = new ArrayList<>();
        List<Double> MESIA_code_result = new ArrayList<>();

        MESIAcalculator calculator = new MESIAcalculator("javaProbability.json");
        int linenum=0;
        for (String r : lines) {
            JSONObject line = JSON.parseObject(r);
            String prediction = line.getString("predictions").substring(2, line.getString("predictions").length() - 2);
            String reference = line.getString("references").substring(2, line.getString("references").length() - 2);
            String code = codelines[linenum];
            linenum++;

            // wrap the method into a class for parsing
            String pre = "public class A { ";
            String tail = " }";
            String ClassCode = pre+code+tail;

            methodName= NLPUtil.getJavaMethodName(code);
            variableNames.clear();
            invokedMethodNames.clear();
            codeParse(ClassCode);
            List<String> codeFeatures = new ArrayList<>();
            codeFeatures.addAll(NLPUtil.camelSplit(NLPUtil.snakeSplit(methodName)));

            double mesia = calculator.getMESIA(reference,codeFeatures);
            MESIA_result.add(mesia);


            for(String inv: invokedMethodNames){
                codeFeatures.addAll(NLPUtil.camelSplit(NLPUtil.snakeSplit(inv)));
            }
            mesia = calculator.getMESIA(reference,codeFeatures);
            MESIA_invocation_result.add(mesia);


            for(String inv: variableNames){
                codeFeatures.addAll(NLPUtil.camelSplit(NLPUtil.snakeSplit(inv)));
            }
            mesia = calculator.getMESIA(reference,codeFeatures);
            MESIA_variable_result.add(mesia);

            String[] codesplit = code.split(" ");
            for(String codepiece:codesplit){
                codeFeatures.addAll(NLPUtil.camelSplit(NLPUtil.snakeSplit(codepiece)));
            }
            mesia = calculator.getMESIA(reference,codeFeatures);
            MESIA_code_result.add(mesia);
        }

        ReadWriteUtil.writeFile("MESIA.json", JSON.toJSONString(MESIA_result, true));
        ReadWriteUtil.writeFile("MESIA_invocation.json", JSON.toJSONString(MESIA_invocation_result, true));
        ReadWriteUtil.writeFile("MESIA_variable.json", JSON.toJSONString(MESIA_variable_result, true));
        ReadWriteUtil.writeFile("MESIA_code.json", JSON.toJSONString(MESIA_code_result, true));
    }
}
