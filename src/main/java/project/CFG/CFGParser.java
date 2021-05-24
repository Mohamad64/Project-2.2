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
    private int ruleID;
    public static String textField;

    public CFGParser(Path path){
        try {
            this.grammar = Files.readAllLines(path);
            this.baseRules = this.parseRules();
            this.convertCFGtoCNF();

            this.languages = this.parseActions();
        }
        catch(IOException e){}
    }

    public CFGParser(){
        this.grammar = Arrays.asList(textField.split("\n"));
        this.baseRules = this.parseRules();
        this.convertCFGtoCNF();
        this.languages = this.parseActions();
    }

    public static String getRespond(String question){
        CFGParser chatbotCFG = new CFGParser();
        return chatbotCFG.response(question);
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

    public Map<String, List<String>> parseRules() {
        Map<String, List<String>> rules = new TreeMap<String, List<String>>();
        List<String> inputs = this.getRules();
        // for each rule in the definition
        for(int i = 0; i<inputs.size(); i++){
            String[] arr = inputs.get(i).trim().split(" ");
            String nonTerminal = arr[0];
            inputs.set(i, inputs.get(i).substring(nonTerminal.length()+1));
            //inputs.get(i).replaceAll(nonTerminal, " ");
            String[] rules_arr = inputs.get(i).split(" \\| ");
            System.out.println(Arrays.deepToString(rules_arr));
            LinkedList<String> condition_list = new LinkedList<String>();
            for (int k = 0; k < rules_arr.length; k++) {
                condition_list.add(rules_arr[k].trim());
            }
            rules.put(nonTerminal, condition_list);
        }
        return rules;
    }


    public List<ContextFreeGrammar> parseActions() {
        List<String> actions = this.getActions();
        List<ContextFreeGrammar> languages = new LinkedList<>();
        for(String action: actions){
            TreeMap<String, List<String>> rules = new TreeMap<>();
            rules.putAll(baseRules);
            String[] arr = action.trim().split(" ");
            ContextFreeGrammar language = new ContextFreeGrammar(arr[0], rules);//arr[0]
            System.out.println(arr[0]);//arr[0]="<LOCATION>"
            System.out.println(language.start);
            StringBuilder response = new StringBuilder();
            for(int i = 2; i<arr.length; i++){
                // replace rule with specific value
                if(arr[i].contains("<") && arr[i].contains(">")){
                    String nonTerminal = arr[i];
                    List<String> value = Arrays.asList(arr[i+1]);
                    language.rules.replace(nonTerminal, value);
                    i++; // jump
                }
                else{
                    // the rest is the response
                    response.append(arr[i]).append(" ");

                }
            }
            language.response = response.toString().trim();
            languages.add(language);
        }
        return languages;
    }

    public void convertCFGtoCNF() {
        // retrieve all non-terminal symbols from the rules
        List<String> nonTerminals = List.copyOf(this.baseRules.keySet());

        // eliminate the start symbol from right-hand sides
        // baseRules.put("START", Arrays.asList());

        // Eliminate rules with nonsolitary terminals
        for(String nonTerminal: nonTerminals){
            List<String> rightSide = baseRules.get(nonTerminal);
            List<String> collectedRules = new LinkedList<>();
            for(String rule: rightSide) {
                String[] arr = rule.trim().split(" ");
                if(arr.length>1){// 1?
                    for(int i = 0; i<arr.length; i++){
                        if(!arr[i].contains(">") && !arr[i].contains("<")){
                            String newKey = "<" + arr[i].toUpperCase(Locale.ROOT) + ">";
                            if(!baseRules.containsKey(newKey)) {
                                // <IS> -> is
                                baseRules.put(newKey, new LinkedList<String>(Arrays.asList(arr[i])));
                            }
                            arr[i] = newKey;
                        }
                    }
                    collectedRules.add(String.join(" ", arr));
                    baseRules.replace(nonTerminal, collectedRules);
                }
            }
        }

        nonTerminals = List.copyOf(this.baseRules.keySet());

        ruleID = 0;
        // <0>, <1>
        // Eliminate right-hand sides with more than 2 nonterminals
        TreeMap<String, List<String>> chomskyRules = new TreeMap<>();

        for(String nonTerminal: nonTerminals){
            List<String> rightSide = baseRules.get(nonTerminal);
            for(int ruleIdx=0; ruleIdx<rightSide.size(); ruleIdx++){
                String[] symbols = rightSide.get(ruleIdx).split(" ");
                // symbols: <WHERE> <IS> <HAMSTER>

                //Traverse symbols, while incrementing the index by 2
                while(symbols.length>2){
                    if(symbols.length%2 == 0){
                        String arr[] = new String[(symbols.length)/2];
                        for(int p = 0; p< symbols.length; p+=2) {
                            chomskyRules.put("<" + ruleID + ">", Arrays.asList(symbols[p] + " " + symbols[p+1]));
                            arr[p/2] = "<" + ruleID + ">";
                            ruleID++;
                        }
                        symbols = arr;
                    } else {
                        String arr[] = new String[(symbols.length + 1)/2];
                        for (int p = 0; p<symbols.length-1; p+=2){//int m = 0, m<symbols.length-1; m+=2){
                            chomskyRules.put("<" + ruleID + ">", Arrays.asList(symbols[p].trim() + " " + symbols[p+1].trim()));
                            arr[p/2] = "<" + ruleID + ">";
                            ruleID++;
                        }
                        chomskyRules.put("<" + ruleID + ">", Arrays.asList(symbols[symbols.length-1]));
                        arr[arr.length-1] = "<" + ruleID + ">";
                        ruleID++;
                        symbols = arr;
                    }
                }
                rightSide.remove(ruleIdx);
                rightSide.add(ruleIdx, String.join(" ", symbols));
                baseRules.replace(nonTerminal, rightSide);
                baseRules.putAll(chomskyRules);


            }
        }

        this.baseRules = eliminateUnitary(this.baseRules);

    }

    public Map<String, List<String>> eliminateUnitary(Map<String, List<String>> rules){
        // A -> B where B -> X1,X2 transforms to A -> X1,X2
        List<String> nonTerminals = List.copyOf(rules.keySet());

        // eliminate unitary rules
        for(String nonTerminal: nonTerminals){
            List<String> list = rules.get(nonTerminal);

            // eliminate A -> B by  B -> X1X2 make A -> X1,X2
            if(list != null) {
                if (list.size() == 1) {
                    if (list.get(0).contains("<") && list.get(0).contains(">")) {//list.contains("<") && list.contains(">")
                        String room = list.get(0);
                        // list.get(0) //B
                        List<String> deepspace = rules.get(room); //X1,X2
                        if(deepspace != null) {
                            rules.replace(nonTerminal, deepspace);
                        }
                    }
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
        //chatbotCFG.baseRules = chatbotCFG.parseRules();
        //chatbotCFG.convertCFGtoCNF();
        //System.out.println(chatbotCFG.baseRules);
        System.out.println(chatbotCFG.response("Where is DeepSpace"));
        /*try {
            System.out.println(chatbotCFG.languages.get(0).accepts("Where is DeepSpace"));
        }
        catch(ContextFreeGrammar.CNFException e){}*/
        //System.out.println(chatbotCFG.languages.get(3).produceRandom());*/

        //String response = chatbotCFG.response("Where is DeepSpace");
        //System.out.println(response);
    }

}
