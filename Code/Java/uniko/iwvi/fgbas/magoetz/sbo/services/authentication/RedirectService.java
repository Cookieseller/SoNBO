package uniko.iwvi.fgbas.magoetz.sbo.services.authentication;

import java.io.IOException;
import java.io.Serializable;

import javax.faces.context.FacesContext;


import uniko.iwvi.fgbas.magoetz.sbo.caching.BeanCache;

import com.ibm.xsp.designer.context.XSPContext;

public class RedirectService implements Serializable {

	private static final long serialVersionUID = 1L;
	private BeanCache localCache;

	public RedirectService() {
        FacesContext ctx = FacesContext.getCurrentInstance(); 
        localCache = (BeanCache) ctx.getApplication().getVariableResolver().resolveVariable(ctx, "localCache");
	}

	public void redirectToLastPage() {
    	FacesContext ctx   = FacesContext.getCurrentInstance(); 
        String redirectUrl = "https://devil.bas.uni-koblenz.de/SoNBO/SNBO-NAV.nsf/main.xsp";

        if (localCache.contains("redirectFromUrl")) {
        	redirectUrl = localCache.get("redirectFromUrl").getValue();
        }
        try {
        	ctx.getExternalContext().redirect(redirectUrl);	
        } catch (IOException e) {
        	e.printStackTrace();
        }
	}

    public void redirectToAuthentication() {
    	FacesContext ctx   = FacesContext.getCurrentInstance(); 
        XSPContext context = XSPContext.getXSPContext(ctx);

        localCache.put("redirectFromUrl", context.getUrl().getAddress());
        try {
        	ctx.getExternalContext().redirect("https://devil.bas.uni-koblenz.de/SoNBO/SNBO-NAV.nsf/auth.xsp");	
        } catch (IOException e) {
        	e.printStackTrace();
        }
	}
}
