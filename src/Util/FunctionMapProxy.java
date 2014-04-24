package Util;

import java.util.HashMap;

/**
 * Author: Matt
 * Date: 2/22/14
 * This class is designed to handle maps containing functions. The following is an acceptable usage:
 * <code>
 *     Util.FunctionMapProxy myMap = new Util.FunctionMapProxy(){{
 *          put("Func1",new Executable(){
 *              @Override
 *              public void run(){
 *                  System.out.println("running");
 *              }
 *          });
 *     }};
 *     myMap.run("Func1");
 * </code>
 *
 * @deprecated extraneous given lambda expressions in Java 8.
 */
public class FunctionMapProxy<T> {

    public FunctionMapProxy() {
        functionMap = new HashMap<>();
    }

    /** All functions must extend this class and Override its one method. **/
    public class Executable{
        /** Run the executable without optional arguments **/
        public Object run(Object... args){ throw new ExecutableNotOverwritten();  }
    }

    private final HashMap<T,Executable> functionMap;

    /** Adds a function with a given name to the map
     *
     * @param func The function name
     * @param executable The function
     */
    public void put(T func,Executable executable){
        functionMap.put(func,executable);
    }

    /** Executes the associated function
     *
     * @param func The name of the function
     */
    public Object run(T func,Object... args){
        return functionMap.get(func).run(args);
    }

    /** Empties all functions **/
    public void emptyMap(){functionMap.clear();}

    /** Returns true iff the function id is recognized **/
    public boolean containsKey(T key){return functionMap.containsKey(key);}

    //Test the class
    public static void main(String[] args){
        FunctionMapProxy<String> myMap = new FunctionMapProxy<String>(){{
            put("Func1",new Executable(){
                @Override
                public Object run(Object... o){
                    System.out.println("running");
                    return null;
                }
            });
        }};
        myMap.run("Func1");
    }

}

class ExecutableNotOverwritten extends RuntimeException{}