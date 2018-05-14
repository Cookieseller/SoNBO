package uniko.iwvi.fgbas.magoetz.sbo.services;

import java.io.Serializable;

import com.ibm.sbt.services.client.ClientServicesException;
import com.ibm.sbt.services.client.connections.profiles.Profile;
import com.ibm.sbt.services.client.connections.profiles.ProfileService;

public class ConnectionsService implements Serializable {

    private static final long serialVersionUID = 1L;

    private String endpoint;

    private ProfileService profileService;

    private Profile myProfile;

    public ConnectionsService(String endpoint) {
        this.endpoint = endpoint;
        this.profileService = new ProfileService(this.endpoint);
    }

    public String getUserEmail() {
        String email = null;
        if (this.myProfile == null) {
            try {
                this.myProfile = this.profileService.getMyProfile();
            } catch (ClientServicesException e) {
                e.printStackTrace();
            }
            email = myProfile.getEmail();
            System.out.println("User EMail: " + email);
        } else {
            email = myProfile.getEmail();
        }
        return email;
    }
}
