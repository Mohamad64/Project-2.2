package project.NLP;

import org.pmml4s.model.Model;


import java.nio.file.Paths;
import java.util.*;

public class ToJava {
    private final Model model = Model.fromFile(String.valueOf(Paths.get("Models/model.pmml")));

    public Double getRegressionValue(String input) {
        Object[] valuesMap = {input};
        Object[] result = model.predict(valuesMap);
        return (Double) result[0];
    }

    public static void main(String[] args) {
        ToJava main = new ToJava();
        String input = "Where is Madrid located";
        double predicted = main.getRegressionValue(input);
        System.out.println(predicted);
    }
}