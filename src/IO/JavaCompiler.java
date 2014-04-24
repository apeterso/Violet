package IO;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import sun.net.www.content.text.plain;

import java.io.*;

/**
 * Author: Matt
 * Date: 2/22/14
 * This class is designed to compile and execute Java code.
 */
public class JavaCompiler {

    public static final SimpleStringProperty output = new SimpleStringProperty();
    public static final SimpleStringProperty severityProperty = new SimpleStringProperty();
    public static final SimpleBooleanProperty outputToggler = new SimpleBooleanProperty();
    private static int hasCompiled;

    /** Returns whether the last compilation was successful, and the program can be executed
     * @return Whether the last compilation was successful
     */
    public static boolean hasCompiled(){ return hasCompiled == 0; }

    /** Runs a Java Program. Returns the exit value. **/
    private static void runProcess(String command){
        Process process = null;
        String pathName = command.split("\0")[1];
        File dir = new File(new File(pathName).getParent());
        String fileName = (new File(command)).getName();
        String suffix = "java";
        if (command.split("\0")[0].equals(suffix))
            fileName = fileName.replace("."+ suffix,"");

        try{
            process = Runtime.getRuntime().exec(command.split("\0")[0]+" "+fileName,null, dir);
        }catch(IOException e){ reportThrowable(e); }

        StreamWrapper errorGobbler = new StreamWrapper(process.getErrorStream());
        StreamWrapper outputGobbler = new StreamWrapper(process.getInputStream());

        errorGobbler.start();
        outputGobbler.start();

        //Wait for threads
        int exitValue = 0;
        try{
            exitValue = process.waitFor();
        }catch(InterruptedException ie){reportThrowable(ie);}

        //report errors and whatnot
        if (errorGobbler.hasError())
            reportThrowable(errorGobbler.getError());
        else
            report(errorGobbler.getOutput(), "ERROR");
        if (outputGobbler.hasError())
            reportThrowable(outputGobbler.getError());
        else
            report(outputGobbler.getOutput(), "NORMAL");

        //reoprt exit value if interesting
        if (exitValue != 0)
            report("\nExit Value : " + String.valueOf(exitValue) + "\n","WARNING");

        hasCompiled = exitValue;
    }

    /** Runs a Java Program by Program Name
     * @param programName The name of the program
     */
    public static void runProgram(String programName){
        //fix invalid path
        runProcess("javac\0" + programName);
        if (hasCompiled())
            runProcess("java\0" + programName.replace(".java",""));
    }



    /** Requests a print-out of the string to the appropriate location
     * @param input The string to output
     * @param severity The severity of the message
     */
    public static void report(String input, String severity){
        severityProperty.set(severity);
        output.set(input);
    }

    /** Requests a clear on the output center
     */
    public static void clearOutput(){outputToggler.set(!outputToggler.get());}

    /** Converts a Throwable to a string and reports it
     * @param t The Throwable to report
     */
    public static void reportThrowable(Throwable t){
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        t.printStackTrace(printWriter);
        report(stringWriter.toString(),"ERROR");
    }


}
