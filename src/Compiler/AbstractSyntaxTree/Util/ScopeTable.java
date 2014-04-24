package Compiler.AbstractSyntaxTree.Util;

import Compiler.Nodes.ASTNode;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

/**
 * This set manages scopes for the AST. It manages the scopes of
 * individual methods and clauses. It is robust and flexible.
 *
 * <br>However, this is fail-slow structure, allowing
 * you to pass the "wrong" node in certain circumstances with ostensibly no consequence.
 * This class is intended for proper use, and makes no guarantees on operation when
 * nodes are passed in where they should not be; in the end, such actions will
 * unlikely result in more than a Null-Pointer Exception later down the road, but
 * they are still to be avoided, certainly.
 *
 * <br>The scope table can be instantiated with a set of default values, like
 * <i>Object</i>, that would otherwise be unsuitable to add to a scope. These
 * are placed in the "0" scope, and individual methods detail their treatment
 * of the default values.
 * <br>Created by Matt Levine on 3/19/14.
 */
public final class ScopeTable {

    private final Stack<HashMap<String,ASTNode>> scopes;
    private final HashSet<String> defaults;

    /** Constructs a new ScopeTable */
    public ScopeTable(){
        scopes = new Stack<>();
        defaults = new HashSet<>();
    }

    /** Constructs a new ScopeTable with the given "built-in" types*/
    public ScopeTable(String... defaultTypes){
        scopes = new Stack<>();
        defaults = new HashSet<>();
        defaults.addAll(Arrays.asList(defaultTypes));
    }

    /** Moves one level deeper **/
    public void incept(){
        scopes.add(new HashMap<>());
    }

    /** Drops the last level **/
    public void wakeUp(){
        scopes.pop();
    }

    /** Returns the number of layers of inception (does not
     * include default scope
     * @return number of scopes
    **/
    public int size(){return scopes.size();}

    /** Adds the node to the current scope
     * @param key the id of the node
     * @param node the data to add
     */
    public void add(String key, ASTNode node){
        scopes.peek().put(key,node);
    }

    /** Looks up the given key in the current scope and all
     * upper scopes - returns 0 for default scope.
     * @param key the name of the item to lookup
     * @return the level of the scope or -1 if it is not found
     */
    public int indexOf(String key){
        if (defaults.contains(key)) return 0;
        for (int i = scopes.size(); i > 0; i--){
            if (scopes.get(i-1).containsKey(key))
                return i;
        }
        return -1;
    }

    /** Looks up the given key in the given layer; if zero looks up in default.
     * @param key the name of the item to lookup
     * @param layer the layer to lookup in
     * @return true if the item is found
     */
    public boolean isInScope(String key, int layer){
        return layer == 0 ? defaults.contains(key) : scopes.get(layer).containsKey(key);
    }

    /** Looks up the given key in the current scope and all
     * upper scopes. Will not look up default table.
     * @param key the name of the item to lookup
     * @return the object with the key name or null if it is not found
     */
    public Object get(String key){
        for (int i = scopes.size(); i > 0; i--){
            if (scopes.get(i-1).containsKey(key))
                return scopes.get(i-1).get(key);
        }
        return null;
    }

}
