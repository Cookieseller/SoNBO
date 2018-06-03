package uniko.iwvi.fgbas.magoetz.sbo.services.authentication;

import java.util.HashMap;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import uniko.iwvi.fgbas.magoetz.sbo.SoNBOSession;

import com.ibm.xsp.complex.Parameter;
import com.ibm.xsp.component.xp.XspEventHandler;

public class AuthenticationListener implements javax.faces.event.ActionListener {

	public void processAction(ActionEvent event) throws AbortProcessingException {
		XspEventHandler eventHandler = (XspEventHandler) event.getSource();
        List<Parameter> params = eventHandler.getParameters();
        
        FacesContext ctx = FacesContext.getCurrentInstance(); 
        SoNBOSession session = (SoNBOSession) ctx.getApplication().getVariableResolver().resolveVariable(ctx, "soNBOSession");

        HashMap<String, String> parameters = new HashMap<String, String>();
        for (Parameter p : params) {
        	parameters.put(p.getName(), p.getValue());
        }
        
        if (parameters.containsKey("user") && parameters.containsKey("password")) {
        	session.updateCredentials(parameters.get("user"), parameters.get("password"));
        }

        new RedirectService().redirectToLastPage();
	}
}