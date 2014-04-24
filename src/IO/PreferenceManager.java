package IO;

import java.util.Arrays;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Author: Matt
 * Date: 2/22/14
 * This class is designed to manage program preferences.
 */
public class PreferenceManager {
    private final Preferences preferences;
    private final String[] backupLocations;
    private int curBackupPos;
    //Hard code the maximum number of backups, could make dynamic
    private static final int maxBackups = 10;

    /** Constructs a new PreferenceManager
     * with default settings.
     */
    public PreferenceManager(){
        //initialize preference module
        preferences = Preferences.userRoot().node(
                this.getClass().getName() );

        //load backup locations
        backupLocations = new String[maxBackups];
        curBackupPos = preferences.getInt("curBackupPos",0);
        String backup;

        int i = 0;
        while ( (backup = preferences.get("bkg"+String.valueOf(i),null))
                != null && i < maxBackups){
            backupLocations[i] = backup;
            i++;
        }

    }

    /** Removes all backup entries
     */
    public void removeBackupEntries(){
        for (int i = 0; i < maxBackups; i++) backupLocations[i] = null;
        try{
            preferences.clear();
            curBackupPos = 0;
        }catch(BackingStoreException bse){
            System.out.println("Err: couldn't clear preferences " + bse.toString());
        }
    }

    /** Sets the backup location for reloading files. If all slots are full, overwrites
     * oldest slot. If backup is already contained, returns quietly.
     * @param backupLocation The backup location
     */
    public void setBackupLocation(String backupLocation){
        if (containsBackup(backupLocation)) return;
        if (curBackupPos == maxBackups) curBackupPos = 0;
        backupLocations[curBackupPos] = backupLocation;
        preferences.put("bkg"+String.valueOf(curBackupPos),backupLocation);
        curBackupPos++;
    }

    /** Returns true iff the given backup is already contained in the preferences
     *
     * @param backup The backup to verify
     * @return True iff the backup is contained
     */
    public boolean containsBackup(String backup){
        return Arrays.asList(backupLocations).contains(backup);
    }

    /** Gets the ith backup location, returns null
     * if no backup location at that index is known (maximum number of locations
     * is inoperably 5).
     * @param i The index of the backup
     * @return The name of the location
     */
    public String getBackupLocation(int i){
        if (i < maxBackups )
            return backupLocations[i];
        return null;
    }

    /** Returns a String representation of the backups **/
    public String toString(){
        String s = "";
        for (int i = 0; i < maxBackups; i++ )
            s += String.valueOf(backupLocations[i]) + "\n";
        return s;
    }

    public void finalize() throws Throwable{
        preferences.putInt("curBackupPos", curBackupPos);
        super.finalize();
    }

    /** Adds current directory to the preference manager **/
    public void saveDirectory(String directory){
        preferences.put("directory",directory);
    }

    /** Loads current directory from the preferences manager **/
    public String loadDirectory(){
        return preferences.get("directory",System.getProperty("user.dir"));
    }

}
