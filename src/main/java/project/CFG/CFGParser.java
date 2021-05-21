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
    private static final String ACTION_INDICATOR = "Action";
    private static final String RULE_INDICATOR = "Rule";
    public static String DEFAULT_RESPONSE = "I have no idea";

    private List<String> grammar;
    private Map<String, List<String>> baseRules;
    private List<ContextFreeGrammar> languages;

    public CFGParser(Path path){
        try {
            this.grammar = Files.readAllLines(path);
            this.baseRules = this.parseRules();
            this.convertCFGtoCNF();

            this.languages = this.parseActions();
        }
        catch(IOException e){}
    }

    public CFGParser(String text){

    }

    private List<String> getRules() {
        // take all lines where a rule is defined and remove the indicator
        return this.grammar.stream().filter(line -> line.startsWith(RULE_INDICATOR))
                .map(line -> line.substring(RULE_INDICATOR.length()))
                .collect(Collectors.toList());
    }

    private List<String> getActions() {
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


    public String response(String question){
        for(ContextFreeGrammar language: languages){
            try {
                // first suitable answer
                if (language.accepts(question)) {
                    return language.response;
                }
            } catch(ContextFreeGrammar.CNFException e){
                System.out.println(e);
            }
        }
        return DEFAULT_RESPONSE;
    }

    public static void main(String[] args){
        CFGParser chatbotCFG = new CFGParser(Paths.get("datasets/manual.cfg"));
        chatbotCFG.baseRules = chatbotCFG.parseRules();
        chatbotCFG.convertCFGtoCNF();
        System.out.println(chatbotCFG.baseRules);
        /*try {
            chatbotCFG.languages.get(3).accepts("Where is DeepSpace");
        }
        catch(ContextFreeGrammar.CNFException e){}
        System.out.println(chatbotCFG.languages.get(3).produceRandom());*/

        //String response = chatbotCFG.response("Where is DeepSpace");
        //System.out.println(response);
    }

}
