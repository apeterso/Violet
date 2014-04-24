package IO;



import java.io.*;

/**
 * Author: Matt
 * Date: 2/22/14
 * This class is designed to manage IO
 */
public class IOManager {

    /** Saves data to a file
     *
     * @param file The file to be saved to
     */
    public static void saveFile( File file, String text ){
        BufferedWriter writer = null;
        if (file != null){
            try{
                writer = new BufferedWriter(
                        new OutputStreamWriter( new FileOutputStream(file),"UTF-8")
                );
                writer.write(text);
            }catch(IOException e){ System.out.println("File Failed to Save"); }
            finally{ try{writer.close();} catch(IOException e2){
                System.out.println("Err: File in use.");}
            }
        }
    }

    /** Saves data to the backup file
     * @param text The information to save
     */
    public void backup(String text){
    }

    /** Loads data from a file
     *
     * @param file The file to be loaded from
     * @return String the text from the file
     */
    public static String loadFile( File file ){
        BufferedReader reader = null;
        String text = "";
        String line;
        if (file != null){
            try{
                reader = new BufferedReader(
                        new InputStreamReader( new FileInputStream(file),"UTF-8")
                );
                //not efficient string manipulation but shouldn't matter
                while ((line = reader.readLine()) != null ){
                    text += line+"\n";
                }
                try{
                    text = text.substring(0,text.length()-1); //strip last \n
                }catch(StringIndexOutOfBoundsException sieobe){ //shouldn't be caught
                    System.out.println("Unexpected Loading Err:" +sieobe+".\nMay indicate systemic problem.");
                    sieobe.printStackTrace();
                }
            }catch(IOException e){ System.out.println("File Failed to Load "+file); }
            finally{ try{reader.close();} catch(IOException e2){
                System.out.println("Err: File in use.");}
            }
        }
        return text;
    }
}
