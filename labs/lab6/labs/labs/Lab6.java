package labs;

import ItemsetClasses.ItemSet;
import ItemsetClasses.Rule;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lab6 {

    private static final String transactionFilePath = "src/labs/files/shopping_data.txt";
    private static final String expectedRulesFilePath = "src/labs/files/expected.txt";
    private static final double minSup = 0.01;
    private static final double minConf = 0.99;

    private static ArrayList<ItemSet> transactions;
    private static ItemSet allItems;
    private static HashMap<Integer, ArrayList<ItemSet>> frequentItemSets;
    private static HashMap<ItemSet, Double> supportMap;
    private static ArrayList<Rule> rules;

    public static void main(String[] args) {
        allItems = new ItemSet();
        transactions = new ArrayList<>();
        frequentItemSets = new HashMap<>();
        supportMap = new HashMap<>();
        processTransactions(transactionFilePath);
        int k = 1;
        while (findFrequentItemSets(k)) {
            k++;
        }
        rules = new ArrayList<>();
        generateRules();
        System.out.println(rules);
        // Load up expected rules and compare them with what we've generated
        HashSet<Rule> expected = parseRuleSet(expectedRulesFilePath);
        if (rules.containsAll(expected) && rules.size() == expected.size()) {
            System.out.println("Rules match expected.");
        } else {
            if (rules.size() == expected.size()) {
                System.out.println("Rule list lengths match: " + rules.size());
            }
            for (Rule rule : rules) {
                if (!expected.contains(rule)) {
                    System.out.println("Unexpected rule: " + rule);
                }
            }
            for (Rule rule : expected) {
                if (!rules.contains(rule)) {
                    System.out.println("Missing rule: " + rule);
                }
            }
        }
    }

    private static HashSet<Rule> parseRuleSet(String path) {
        HashSet<Rule> rules = new HashSet<>();
        try {
            File file = new File(path);
            Scanner scanner = new Scanner(file);
            String line = scanner.nextLine();
            Matcher matcher = Pattern.compile("\\[[\\d, ]+\\]->\\[[\\d, ]+\\]").matcher(line);
            while (matcher.find()) {
                Rule rule = Rule.parseRule(matcher.group());
                rules.add(rule);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.printf("File %s not found\n", path);
        }
        return rules;
    }

    /**
     * @param itemSet An ItemSet from which to generate all association rules with 1-item consequents.
     * @return A list 1-item consequent rules.
     */
    private static ArrayList<Rule> genSingleConsequentRules(ItemSet itemSet) {
        ArrayList<Rule> rules = new ArrayList<>();
        for (ItemSet left : itemSet.subsetsOfSize(itemSet.size() - 1)) {
            ItemSet right = itemSet.copy();
            right.removeAll(left);
            rules.add(new Rule(left, right));
        }
        return rules;
    }

    /**
     * Generates all valid association rules for ItemSets in frequentItemSets.
     */
    private static void generateRules() {
        List<ItemSet> listOfFrequentItemSets = new ArrayList<>();
        frequentItemSets.entrySet().stream()
                .filter(entry -> entry.getKey() >= 2)  // k>=2; don't want singleton sets
                .map(Map.Entry::getValue)
                .forEach(listOfFrequentItemSets::addAll);
        for (ItemSet itemSet : listOfFrequentItemSets) {
            List<Rule> ruleList = genSingleConsequentRules(itemSet).stream()
                    .filter(Lab6::isMinConfidenceMet)
                    .toList();
            Lab6.rules.addAll(ruleList);
            // ArrayList<Rule> rules = new ArrayList<>(ruleList); // rules needs to be mutable
            List<ItemSet> consequents = ruleList.stream().map(Rule::right).toList();
            aprioriGenRules(itemSet, consequents);
        }
    }

    private static void aprioriGenRules(ItemSet itemSet, List<ItemSet> consequents) {
        if (consequents.isEmpty() || itemSet.size() <= consequents.getFirst().size()) {
            return;
        }
        ArrayList<ItemSet> candidates = generateCandidates(consequents);
        for (ItemSet candidate : (ArrayList<ItemSet>) candidates.clone()) { // clone to avoid concurrent modification
            Rule ruleCandidate = new Rule(itemSet.withoutAny(candidate), candidate);
            if (isMinConfidenceMet(ruleCandidate)) {
                Lab6.rules.add(ruleCandidate);
            } else {
                candidates.remove(candidate);
            }
        }
        aprioriGenRules(itemSet, candidates);
    }

    /**
     * @param r A rule to check the confidence of
     * @return True if the rule has at least minimum confidence; false otherwise.
     */
    private static boolean isMinConfidenceMet(Rule r) {
        double confidence = calcConfidence(r);
        return confidence >= minConf;
    }

    /**
     * Calculate confidence for a rule.
     * For a rule A -> B, confidence is defined as
     * <p> {@code support(A U B) / support(A)} </p>
     */
    private static double calcConfidence(Rule r) {
        double unionSupport = getSupport(r.getUnion());
        double leftSupport = getSupport(r.left());
        return unionSupport / leftSupport;
    }

    /**
     * Reads a set of transactions from a provided file.
     * Expected format is comma-separated lists of integers, each separated by newlines.
     * The first number in each line is the transaction number, and following numbers are purchased items.
     *
     * @param path the path to the file to read transactions from.
     */
    private static void processTransactions(String path) {
        try {
            File file = new File(path);
            Scanner scanner = new Scanner(file);
            String line;
            while (scanner.hasNextLine()) {
                line = scanner.nextLine();
                String[] tokens = line.split(", ");
                ItemSet items = new ItemSet();
                // Skip first element since it is the transaction ID
                for (String itemStr : Arrays.stream(tokens).toList().subList(1, tokens.length)) {
                    int item = Integer.parseInt(itemStr);
                    // add item to set of all items AND set of items in this transaction
                    items.addItem(item);
                    allItems.addItem(item);
                }
                transactions.add(items); // add transaction ItemSet to list of transactions
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.printf("File %s not found\n", path);
        }
    }

    /**
     * Checks to see if the provided itemset is frequent in the list of transactions.
     *
     * @param itemSet an itemset to check the frequency of
     * @return True if the itemset has at least the minimum support; false otherwise.
     */
    private static boolean isFrequent(ItemSet itemSet) {
        double support = getSupport(itemSet);
        return support >= minSup;
    }

    /**
     * Gets the support for a given ItemSet.
     * If the support has already been calculated, gets it from the supportMap.
     * Otherwise, calls calcSupport and adds the result to the supportMap.
     * @param itemSet The item set to get support for
     * @return the support for the item set
     */
    private static double getSupport(ItemSet itemSet) {
        // TODO: refactor so support is stored in each ItemSet
        if (supportMap.containsKey(itemSet)) {
            return supportMap.get(itemSet);
        }
        double support = calcSupport(itemSet);
        supportMap.put(itemSet, support);
        return support;
    }

    /**
     * Calculates the support for a given ItemSet.
     * @param itemSet The item set to calculate support for
     * @return the support of the item set
     */
    private static double calcSupport(ItemSet itemSet) {
        int appearances = 0;
        for (ItemSet transaction : transactions) {
            if (transaction.contains(itemSet)) {
                appearances++;
            }
        }
        return appearances / (double) transactions.size();
    }

    /**
     * Finds the itemsets that occur in at least {@code minSup} of transactions.
     */
    private static void findFrequentSingleItemSets() {
        frequentItemSets.putIfAbsent(1, new ArrayList<>());
        for (ItemSet singleton : allItems.subsetsOfSize(1)) {
            if (isFrequent(singleton)) {
                frequentItemSets.get(1).add(singleton);
            }
        }
    }

    /**
     * Finds frequent itemsets of size k. Recursively finds size k-1 itemsets if they have not already been found.
     *
     * @param k the size of sets to find
     * @return True if itemsets of the specified size were found; false otherwise
     */
    private static boolean findFrequentItemSets(int k) {
        boolean frequentItemSetsFound = false;
        if (k == 1) {
            findFrequentSingleItemSets();
            return true;
        }
        if (!frequentItemSets.containsKey(k-1)) {
            findFrequentItemSets(k-1);
        }
        ArrayList<ItemSet> kMinusOneSets = frequentItemSets.get(k-1);
        ArrayList<ItemSet> candidates = generateCandidates(kMinusOneSets);
        for (ItemSet candidate : candidates) {
            if (isFrequent(candidate)) {
                frequentItemSetsFound = true;
                frequentItemSets.putIfAbsent(k, new ArrayList<>());
                frequentItemSets.get(k).add(candidate);
            }
        }
        return frequentItemSetsFound;
    }

    /**
     * Generates size k+1 candidates from a set of size k {@code ItemSets}.
     * Merges sets with the same first k-1 elements; e.g. ABC, ABD => ABCD
     *
     * @param itemSets Frequent itemsets of size k to generate candidates from.
     * @return an ArrayList of size k+1 ItemSets generated by merging elements of the provided list.
     */
    private static ArrayList<ItemSet> generateCandidates(List<ItemSet> itemSets) {
        ArrayList<ItemSet> candidates = new ArrayList<>();
        int kMinusOne = itemSets.getFirst().size() - 1;  // just get size of first itemset
        // Join sets to generate candidates
        for (int i = 0; i < itemSets.size(); i++) {
            ItemSet thisSet = itemSets.get(i);
            for (int j = i + 1; j < itemSets.size(); j++) {
                ItemSet otherSet = itemSets.get(j);
                if (thisSet.getItems().subList(0, kMinusOne).equals(
                        otherSet.getItems().subList(0, kMinusOne))) {
                    ItemSet candidate = thisSet.copy();
                    candidate.addItem(otherSet.getItemSet().last());
                    candidates.add(candidate);
                }
            }
        }
        // Prune candidates that cannot be frequent
        for (ItemSet candidate : candidates) {
            ArrayList<ItemSet> subsets = candidate.subsetsOfSize(kMinusOne);
            // candidates can only be frequent if all subsets are frequent
            for (ItemSet subset : subsets) {
                if (!frequentItemSets.get(kMinusOne).contains(subset)) {
                    candidates.remove(candidate);
                    break;
                }
            }
        }
        return candidates;
    }
}
