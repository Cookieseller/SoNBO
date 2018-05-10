package uniko.iwvi.fgbas.magoetz.sbo.services;

import java.io.Serializable;
import javax.faces.context.FacesContext;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import uniko.iwvi.fgbas.magoetz.sbo.caching.BeanCache;

public class CacheService implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private final BeanCache cache;
    
    /**
     * Pass in the cache to use, currently there is only a query cache but this might be expanded
     *
     * @param cache
     */
    public CacheService(String cache) {
    	this.cache = (BeanCache)ExtLibUtil.resolveVariable(FacesContext.getCurrentInstance(), cache);
    }
    
    public String get(String key) {
    	/*
    	if (cache.contains(key)) {
    		return cache.get(key).getValue();
    	}*/
    	
    	return null;
    }
    
    public void put(String key, String value) {
    	cache.put(key, value);
    }
}