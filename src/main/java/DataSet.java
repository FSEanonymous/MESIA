/**
 * the datasets can be accessed from: https://github.com/wasiahmad/NeuralCodeSum/tree/master/data
 */

public class DataSet {
    public static String projectPath = "D:\\MESIA\\";
    public static String javaDataPath = "D:\\MESIA\\JavaData\\"; // the local path to save the java dataset
    public static String pythonDataPath = "D:\\MESIA\\PythonData\\"; // the local path to save the python dataset

    public static String getDataSetPath(String language){
        if(language.equals("java"))return javaDataPath;
        if(language.equals("python"))return pythonDataPath;
        return null;
    }
}
