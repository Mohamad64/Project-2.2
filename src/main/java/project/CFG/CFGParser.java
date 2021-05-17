package project.CFG;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CFGParser {
    String input;
    public CFGParser(String input){
        this.input = input;
    }

    public Map<String, List<String>> parse(String input) {
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
        CFGParser test_parser = new CFGParser("");

    }

}
