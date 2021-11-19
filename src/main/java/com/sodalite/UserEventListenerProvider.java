package com.sodalite;

import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.ResourceType;
import org.keycloak.models.ClientModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RealmProvider;
import org.keycloak.models.RoleModel;
import org.keycloak.models.UserModel;
import org.jboss.logging.Logger;

public class UserEventListenerProvider implements EventListenerProvider {

    private static final Logger log = Logger.getLogger(UserEventListenerProvider.class);

    private final KeycloakSession session;
    private final RealmProvider model;

    private final String[] role_templates = {"%s_aadm_r", "%s_aadm_w", "%s_rm_r", "%s_rm_w"};
    private final String[] default_namespaces = {"openstack", "docker"};
    private final String default_client_id = "sodalite-ide";

    public UserEventListenerProvider(KeycloakSession session) {
        this.session = session;
        this.model = session.realms();
    }

    @Override
    public void onEvent(Event event) {

        if (EventType.REGISTER.equals(event.getType())) {
            log.infof("New registration event handled");
            RealmModel realm = this.model.getRealm(event.getRealmId());
            UserModel newRegisteredUser = this.session.users().getUserById(realm, event.getUserId());
            ClientModel client = this.session.clients().getClientByClientId(realm, default_client_id);
            log.infof("New user registered %s", newRegisteredUser.getUsername());
            for (String role_template : role_templates)
            {
                String role_name = String.format(role_template, newRegisteredUser.getUsername());
                RoleModel role = this.session.roles().addClientRole(client, role_name);
                newRegisteredUser.grantRole(role);
                log.infof("New role created and granted %s", role_name);
            }

            for (String namespace : default_namespaces)
            {
                for (String role_template : role_templates)
                {
                    String role_name = String.format(role_template, namespace);
                    RoleModel role = this.session.roles().getClientRole(client, role_name);
                    if (role != null)
                    {
                        newRegisteredUser.grantRole(role);
                        log.infof("Default role granted %s", role_name);
                    }
                    else
                    {
                        log.infof("Default role not found %s", role_name);
                    }
                }
            }

            log.info("-----------------------------------------------------------");
        }

    }

    @Override
    public void onEvent(AdminEvent event, boolean includeRepresentation) {
    }

    @Override
    public void close() {
    }

}
