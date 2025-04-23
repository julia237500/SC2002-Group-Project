package exception;

import manager.interfaces.DataManager;
import model.DataModel;

/**
 * Thrown to indicate that a failure occurred while attempting to save {@link DataModel} to a file by {@link DataManager}.
 * <p>
 * This is a checked exception to ensure that calling code explicitly handles save failures.
 * If the model has been modified before the save attempt, the caller is responsible
 * for reverting those changes if needed (e.g., rollback or cleanup),
 * except deletion.
 * 
 * @see DataModel
 * @see DataManager
 */
public class DataSavingException extends Exception{
    public DataSavingException(String message){
        super(message);
    }
}
