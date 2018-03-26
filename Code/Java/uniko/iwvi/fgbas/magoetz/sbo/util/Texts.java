package uniko.iwvi.fgbas.magoetz.sbo.util;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;

public class Texts implements Serializable {

    private static final long serialVersionUID = 1L;


    private Locale locale;

    public Texts(Locale locale) {
        this.locale = locale;
    }

    public void addLocalizedInfo(String key, Object... args) {
        addMessage(getMessage(key, args), FacesMessage.SEVERITY_INFO);
    }

    public void addLocalizedWarning(String key, Object... args) {
        addMessage(getMessage(key, args), FacesMessage.SEVERITY_WARN);
    }

    public void addLocalizedError(String key, Object... args) {
        addMessage(getMessage(key, args), FacesMessage.SEVERITY_ERROR);
    }

    public void addLocalizedInfo(String key) {
        addMessage(getMessage(key), FacesMessage.SEVERITY_INFO);
    }

    public void addLocalizedWarning(String key) {
        addMessage(getMessage(key), FacesMessage.SEVERITY_WARN);
    }

    public void addLocalizedError(String key) {
        addMessage(getMessage(key), FacesMessage.SEVERITY_ERROR);
    }

    private void addMessage(String message, Severity severity) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, message, null));
    }

    public String getMessage(String key, Object... arguments) {
        MessageFormat mf = new MessageFormat(getMessage(key));
        return mf.format(arguments);
    }

    public String getMessage(String key) {
        try {
            return getBundle().getString(key);
        } catch (MissingResourceException e) {
            return "???_" + key + "_???";
        }
    }

    public ResourceBundle getBundle() {
        return ResourceBundle.getBundle("uniko.iwvi.fgbas.magoetz.sbo.util.translation", locale);
    }
}