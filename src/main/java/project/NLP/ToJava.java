package project.NLP;
import org.pmml4s.model.Model;

import java.util.*;

public class ToJava {
    private final Model model = Model.fromFile(ToJava.class.getClassLoader().getResource("model.pmml").getFile());

    public Double getRegressionValue(Map<String, Double> values) {
        Object[] valuesMap = Arrays.stream(model.inputNames()).map(values::get).toArray();
        Object[] result = model.predict(valuesMap);
        return (Double) result[0];
    }

    public static void main(String[] args) {

    }
}