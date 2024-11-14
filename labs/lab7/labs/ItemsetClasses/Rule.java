package ItemsetClasses;

public record Rule(ItemSet left, ItemSet right) {

    /**
     * Parses a Rule from a string representation following the format of the toString.
     * @param str the Rule string to parse
     * @return a new Rule parsed from the provided string
     * @see Rule.toString
     */
    public static Rule parseRule(String str) {
        String[] split = str.split("->");
        ItemSet left = ItemSet.parseItemSet(split[0]);
        ItemSet right = ItemSet.parseItemSet(split[1]);
        return new Rule(left, right);
    }

    /**
     * Returns a new ItemSet that is a union of this Rule's antecedent (this.left) and consequent (this.right).
     * @return a new ItemSet with all items referred to in the Rule
     */
    public ItemSet getUnion() {
        return left().union(right());
    }

    /**
     * Returns a string representation of the Rule.
     * Output format is <p><code>
     *         [A<sub>1</sub>, A<sub>2</sub>, ..., A<sub>n</sub>]->[B<sub>1</sub>, B<sub>2</sub>, ..., B<sub>n</sub>]
     *     </code></p>
     * @return a string representation of this Rule
     */
    public String toString() {
        return left.toString() + "->" + right.toString();
    }

}
