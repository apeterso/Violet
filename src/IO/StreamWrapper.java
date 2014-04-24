package IO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/** @author Matthew Levin
 * As designed by Michael Daconta in JavaWorld article "When Runtime.exec() won't",
 * this class handles the odd cases where the runtime exec hangs do to either
 * failures in native code or subtle difficulties with the input stream.
 */
class StreamWrapper extends Thread{
    private final InputStream inputStream;
    private final StringBuilder output;
    private Throwable error;

    public StreamWrapper(InputStream inputStream){
        this.inputStream = inputStream;
        output = new StringBuilder();
        error = null;
    }

    /** Returns the string output of the execution.
     * @return The string output of the exeuction
     */
    public String getOutput(){ return output.toString(); }

    /** Returns an error, or null if ne was not discovered
     * @return An error if found
     */
    public Throwable getError(){ return error; }

    /** Returns true iff there is a (non-null) error in execution
     * @return Whether there was an error in execution
     */
    public boolean hasError(){ return error != null; }

    /** Runs the thread**/
    @Override
    public void run(){
        try{
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            while ( (line = bufferedReader.readLine()) != null ){
                output.append("\n").append(line);
            }
        }catch(IOException ioe){
            error = ioe;
        }
    }


}

