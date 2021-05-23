package project.CFG;

import java.util.*;

import static java.lang.System.err;
import static java.lang.System.out;

public class ContextFreeGrammar {

    // starting symbol
    protected String start;

    protected String response;

    // production rules
    protected Map<String, List<String>> rules;

    private Random random = new Random();

    private final PrettyPrinter pretty = new PrettyPrinter(out);

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
    public boolean accepts(String phrase) throws CNFException{
        if(phrase.isEmpty()){
            // "S -> " exist as a production rule
            List<String> rule = rules.get(this.start);

            // accept if rule exists
            return rule.isEmpty();
        }

        if(rules instanceof TreeMap) {
            // split sentence into tokens
            String[] words = phrase.split("\\s+");

            // construct CYK table
            String[][] tableCYK = new String[words.length][words.length];

            // retrieve all non-terminal symbols from the rules
            List<String> nonTerminals = List.copyOf(rules.keySet());

            // enter every token into the first line
            for (int wordIdx = 0; wordIdx < words.length; wordIdx++) {
                // search for matching rule
                for (String nonTerminal : nonTerminals) {
                    List<String> rule = rules.get(nonTerminal);
                    // matching terminal rule
                    if(rule != null) {
                        if (rule.contains(words[wordIdx])) {
                            if(tableCYK[0][wordIdx] == null) {
                                tableCYK[0][wordIdx] = nonTerminal;
                            }
                            else{
                                tableCYK[0][wordIdx] = tableCYK[0][wordIdx] + " " + nonTerminal;
                            }
                        }
                    }
                }
            }
            /*
            [<LOCATION>]                [2,0]
            [<0> <7>  null null]        [1,0    1,1     1,2]
            [where  is  <ROOM>]         [0,0    0,1     0,2]
            combinations: where null (skipped), <0> null, <0> <1> (LOCATION)
             */

            int i = 1; int j=0;
            int temp_i = 0; int temp_j = 0;
            while(i<tableCYK.length) {
                j=0;
                while (j < tableCYK[0].length) {
                    temp_i = i - 1;
                    temp_j = j + 1;
                    LinkedList<String> addingNonterminals = new LinkedList<>();

                    while (temp_i >= 0 && temp_j < tableCYK[0].length) {
                        for (int down = i-1; down >= 0; down--) {
                            // compare tableCYK[i][down] to tableCYK[temp_i][temp_j]


                            if(tableCYK[down][j] == null || tableCYK[temp_i][temp_j] == null){continue;}
                            String[] downTokens = tableCYK[down][j].split(" ");
                            String[] diagTokens = tableCYK[temp_i][temp_j].split(" ");//<0><7>
                            //String suspectedRule = tableCYK[down][j] + " " + tableCYK[temp_i][temp_j];

                            // if any two combinations are a right side in the rules
                            List<String> combinations = cartesianProduct(" ", downTokens, diagTokens);

                            // we add the left side of the rule
                            for (String nonTerminal : nonTerminals) {
                                List<String> rule = rules.get(nonTerminal);
                                for(String suspectedRule: combinations){
                                    if(rule != null) {
                                        if (rule.contains(suspectedRule)) {
                                            // set tableCYK[i][j]
                                            if (tableCYK[i][j] == null) {
                                                tableCYK[i][j] = nonTerminal;
                                            } else {
                                                tableCYK[i][j] = tableCYK[i][j] + " " + nonTerminal;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        temp_i--;
                        temp_j++;
                    }
                    j++;
                }
                i++;
            }

            pretty.print(tableCYK);
            System.out.println(this);

            // create diagonal non-terminal line in CYK table
            /*for(int i=0; i<words.length; i++){
                // for each variable A
                for(String A: nonterminals){
                    // match string words to the rules
                    List<String> right = rules.get(A);
                    for(String S: right) {// right: Which lectures are there <TIMEEXPRESSION>
                        if(words[i].equals(S)){
                            System.out.println(i);
                            System.out.println(S);
                            System.out.println(A);
                            if(tableCYK[i][i] == null) {//
                                tableCYK[i][i] = A;
                            }
                            else {
                                tableCYK[i][i] += " " + A;
                            }
                        }
                    }
                }
                if(tableCYK[i][i] == null){
                    tableCYK[i][i] = words[i];
                }
            }*/

            /*for(int l=1; l<words.length+1; l++) {
                for(int i=0; i<words.length-l+1; i++){
                    int j = i+l-1;
                    for(int k = i; k<j; k++){
                        for(String A: nonterminals){
                            List<String> right = rules.get(A);
                            for(String S: right) {
                                String[] split =  S.split("\\s+");
                                if(split.length == 2) {// always size 2
                                    if(tableCYK[i][k] != null && tableCYK[k+1][j] != null) {
                                        if (tableCYK[i][k].contains(split[0]) && tableCYK[k + 1][j].contains(split[1])) {
                                            if(tableCYK[i][j] == null) {//
                                                tableCYK[i][j] = A;
                                            }
                                            else {
                                                // already in?
                                                if(!tableCYK[i][j].contains(A)) {
                                                    tableCYK[i][j] += " " + A;
                                                }
                                            }
                                            pretty.print(tableCYK);
                                        }
                                    }
                                }
                            }
                        }
                        if(tableCYK[i][j] == null){

                        }
                    }
                }
            }*/

            // last column of first row contains the start symbol
            return tableCYK[words.length-1][0] != null ? tableCYK[words.length-1][0].contains(this.start) : false;

        }
        else{
            throw new CNFException("CYK algorithm expects Chomsky normal form");
        }
    }

    private List<String> cartesianProduct(String delimeter, String[] arr1, String[] arr2){
        String[][] cartesian = new String[arr1.length][arr2.length];
        for(int i=0; i< arr1.length; i++){
            for(int j=0; j<arr2.length; j++){
                cartesian[i][j] = arr1[i] + delimeter + arr2[j];
            }
        }

        LinkedList<String> flattenedCartesianProduct = new LinkedList<>();
        for(String[] array: cartesian){
            flattenedCartesianProduct.addAll(Arrays.asList(array));
        }
        return flattenedCartesianProduct;
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
        Map<String, List<String>> rulesAnimals = new HashMap<String, List<String>>();

        rulesAnimals.put("S", new LinkedList<String>(Arrays.asList("The N V")));
        rulesAnimals.put("N", new LinkedList<String>(Arrays.asList("cat","dog","chicken")));
        rulesAnimals.put("V", new LinkedList<String>(Arrays.asList("meows","barks","clucks")));

        ContextFreeGrammar grammar = new ContextFreeGrammar("S", rulesAnimals);

        for(int i=0; i<3; i++) {
            System.out.println(grammar.produceRandom());
        }

        Map<String, List<String>> rules = new TreeMap<String, List<String>>();

        rules.put("S", new LinkedList<String>(Arrays.asList("A B","B C")));
        rules.put("A" ,new LinkedList<String>(Arrays.asList("B A", "a")));
        rules.put("B" ,new LinkedList<String>(Arrays.asList("C C", "b")));
        rules.put("C", new LinkedList<String>(Arrays.asList("A B", "a")));


        ContextFreeGrammar geekGrammar = new ContextFreeGrammar("S", rules);

        for(int i=0; i<3; i++) {
            System.out.println(geekGrammar.produceRandom());
        }

        // Check if baaba is in L(G)
        try {
            System.out.println(geekGrammar.accepts("b a a b a"));
        }
        catch(CNFException e){
            System.out.println("Sorry, not in chomsky normal form");
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        Iterator<Map.Entry<String, List<String>>> iter = this.rules.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, List<String>> entry = iter.next();
            sb.append(entry.getKey());
            sb.append(" -> ").append('"');
            sb.append(entry.getValue());
            sb.append('"');
            if (iter.hasNext()) {
                sb.append(',').append(' ');
            }
        }
        return sb.toString();

    }
}
