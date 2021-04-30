package project.CFG;

import java.util.*;

public class ContextFreeGrammar {

    // starting symbol
    private String start;

    // production rules
    private Map<String, List<String>> rules;

    private Random random = new Random();

    public ContextFreeGrammar(String start, Map<String, List<String>> rules){
        this.start = start;
        this.rules = rules;
    }

    /* Produce random sentences recursively
            to check a context free grammar.
     */
    public String produce(String symbol, List<String> expansion){
        if(rules.containsKey(symbol)){
            // symbol is non-terminal
            List<String> disjunctions =  rules.get(symbol);

            // expand random alternatives
            String pick = disjunctions.get(random.nextInt(disjunctions.size()));

            // split into conjunctive tokens
            String[] tokens = pick.split("\\s+");

            for(String token: tokens){
                produce(token, expansion);
            }
        }
        else {
            // add terminal symbols
            expansion.add(symbol);
        }
        return String.join(" ", expansion);
    }

    public static void main(String[] args){
        Map<String, List<String>> rules = new HashMap<String, List<String>>();

        String start = "S";

        rules.put("S", new LinkedList<String>(Arrays.asList("The N V")));
        rules.put("N", new LinkedList<String>(Arrays.asList("cat","dog")));
        rules.put("V", new LinkedList<String>(Arrays.asList("meows","barks")));

        ContextFreeGrammar grammar = new ContextFreeGrammar(start, rules);

        for(int i=0; i<3; i++) {
            ArrayList<String> expansion = new ArrayList<>();
            String result = grammar.produce(start, expansion);

            System.out.println(result);
        }
    }
}
