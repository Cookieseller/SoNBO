package uniko.iwvi.fgbas.magoetz.sbo.services;

import java.io.Serializable;
import javax.faces.context.FacesContext;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import uniko.iwvi.fgbas.magoetz.sbo.caching.BeanCache;

public class CacheService implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private final BeanCache cache = (BeanCache)ExtLibUtil.resolveVariable(FacesContext.getCurrentInstance(), "queryCache");
    
    public String get(String key) {
    	if (cache.contains(key)) {
    		return cache.get(key).getValue();
    	}
    	
    	return null;
    }
    
    public void put(String key, String value) {
    	cache.put(key, value);
    }
}