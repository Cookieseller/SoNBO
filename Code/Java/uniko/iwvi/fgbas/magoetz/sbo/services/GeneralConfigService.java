package uniko.iwvi.fgbas.magoetz.sbo.services;

import java.io.Serializable;

import lotus.domino.NotesException;
import uniko.iwvi.fgbas.magoetz.sbo.database.NotesDB;

import com.google.gson.JsonObject;

public class GeneralConfigService implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Return config entry by it's name
     *
     * @param nodeTypeName
     * @return
     * 
     * @throws NotesException 
     */
    public JsonObject getConfigEntryByName(String configEntryName) {

    	NotesDB configDb = new NotesDB();
    	JsonObject configEntry = configDb.getJsonObject("generalConfig", configEntryName, "configEntryJson");

    	return configEntry;
    }
    
    public void updateConfigEntry(String configEntryName, String value) {
    	NotesDB configDb = new NotesDB();
    	configDb.updateConfigEntry(configEntryName, value);
    }
}
