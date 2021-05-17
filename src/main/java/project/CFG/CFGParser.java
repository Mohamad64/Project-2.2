package project.CFG;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class CFGParser {

    // formatting of the .cfg file
    private static final String DEV = "->";
    private static final String ACTION_INDICATOR = "Action";
    private static final String RULE_INDICATOR = "Rule";

    private List<String> grammar;

    public CFGParser(Path path){
        try {
            this.grammar = Files.readAllLines(path);
        }
        catch(IOException e){}
    }

    public List<String> getRules() {
        // take all lines where a rule is defined and remove the indicator
        return this.grammar.stream().filter(line -> line.startsWith(RULE_INDICATOR))
                .map(line -> line.substring(RULE_INDICATOR.length()))
                .collect(Collectors.toList());
    }

    public List<String> getActions() {
        // take all lines where an action is defined and remove the indicator
        return this.grammar.stream().filter(line -> line.startsWith(ACTION_INDICATOR))
                .map(line -> line.substring(ACTION_INDICATOR.length()))
                .collect(Collectors.toList());
    }

    public static Map<String, List<String>> parse(String input) {
        String[] arr = input.split(" ");
        Map<String, List<String>> rules = new TreeMap<String, List<String>>();
        int index = 0;
        //Traverse the whole CFG
        while(index != arr.length){

            while(arr[index] != "Rule" || arr[index] != "Action") {
                index++;
            }
            if(arr[index] == "Rule"){
                int index_i = index + 1;
                while(arr[index_i] != "Rule"){
                    index_i++;
                }
                String conditions = " ";
                for(int i = index + 2; i< index_i; i++) {
                    conditions += " " + arr[i];
                }
                String[] rules_arr = conditions.split(" | ");
                LinkedList<String> condition_list = new LinkedList<String>();
                for(int i = 0; i<rules_arr.length; i++){
                    rules_arr[i].trim();
                    condition_list.add(rules_arr[i]);
                }
                rules.put(arr[index + 1], condition_list);
                index = index_i;
            }

            if(arr[index] == "Action") {
                int index_i = index + 1;
                while(arr[index_i] != "Action"){
                    index_i++;
                }
                String actions = " ";
                for(int i = index + 2; i< index_i; i++) {
                    actions += " " + arr[i];
                }
            }
        }

        return rules;
    }



    public static void main(String[] args){
        CFGParser chatbotDefinition = new CFGParser(Paths.get("datasets/manual.cfg"));
        List<String> rules = chatbotDefinition.getRules();
        System.out.println(rules);
    }

}
