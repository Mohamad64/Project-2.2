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

    public class CNFException extends Exception{
        public CNFException(String message){
            super(message);
        }
    }

    /* Cocke-Younger-Kasami algorithm
        @param  word    is in the CFG defined by this class.
     */
    public boolean accepts(String word) throws CNFException{
        if(rules instanceof TreeMap){
            // construct CYK table
            String[][] tableCYK = new String[word.length()][word.length()];

            // last column of first row contains the start symbol
            return tableCYK[0][word.length() - 1].contains(this.start);
        }
        else{
            throw new CNFException("CYK algorithm expects Chomsky normal form");
        }
    }

    /* Construct a word from this grammar
        by randomly expanding the production rules.
        @param  symbol  the starting symbol to expand from.
        @param  expansion   random construction from production rules
     */
    private String produce(String symbol, List<String> expansion){
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

    public String produceRandom(){
        return produce(this.start, new ArrayList<String>());
    }

    public static void main(String[] args){
        Map<String, List<String>> rules = new HashMap<String, List<String>>();

        rules.put("S", new LinkedList<String>(Arrays.asList("The N V")));
        rules.put("N", new LinkedList<String>(Arrays.asList("cat","dog")));
        rules.put("V", new LinkedList<String>(Arrays.asList("meows","barks")));

        ContextFreeGrammar grammar = new ContextFreeGrammar("S", rules);

        for(int i=0; i<3; i++) {
            System.out.println(grammar.produceRandom());
        }
    }
}
