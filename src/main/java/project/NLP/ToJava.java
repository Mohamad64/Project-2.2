package project.NLP;

import org.pmml4s.model.Model;



import java.nio.file.Paths;
import java.util.*;

public class ToJava {
    private final Model model = Model.fromFile(String.valueOf(Paths.get("/Users/mohamadfayazi/Documents/GitHub" +
            "/Project-2.2/src/main/java/project/NLP" +
            "/svc_tfidf.pmml")));

    public Object getRegressionValue(String input) {
        Object[] valuesMap = {input};
        Object[] result = model.predict(valuesMap);
        return result[0];
    }

    public static void main(String[] args) {
        ToJava main = new ToJava();
        String input = "where is get to SpaceBox";
        Object predicted = main.getRegressionValue(input);
        System.out.println(predicted);
    }
}