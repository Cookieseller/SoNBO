package uniko.iwvi.fgbas.magoetz.sbo.caching;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.AbstractMap.SimpleEntry;

import org.joda.time.DateTime;

public class BeanCache implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private final Map<String, SimpleEntry<DateTime, String>> cache = new HashMap<String, SimpleEntry<DateTime, String>>();
    
    public void put(String key, String value) {
    	
    	if (cache.containsKey(key)) {
    		cache.remove(key);
    	}

    	cache.put(key, new SimpleEntry<DateTime, String>(new DateTime(), value));
    }
    
    public void remove(String key) {
    	if (cache.containsKey(key)) {
    		cache.remove(key);
    	}
    }
    
    public boolean contains(String key) {
    	return cache.containsKey(key);
    }
    
    public SimpleEntry<DateTime, String> get(String key) {
    	if (cache.containsKey(key)) {
    		return cache.get(key);
    	}
    	
    	return null;
    }
}
