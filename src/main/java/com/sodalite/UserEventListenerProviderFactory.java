package com.sodalite;

import org.keycloak.Config;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.jboss.logging.Logger;

public class UserEventListenerProviderFactory implements EventListenerProviderFactory {

  private static final Logger log = Logger.getLogger(UserEventListenerProviderFactory.class);

  @Override
  public EventListenerProvider create(KeycloakSession session) {
    log.info("SODALITE User event provider created");
    return new UserEventListenerProvider(session);
  }

  @Override
  public void init(Config.Scope config) {
  }

  @Override
  public void postInit(KeycloakSessionFactory factory) {
  }

  @Override
  public void close() {
  }

  @Override
  public String getId() {
    return "SODALITE-event-listener";
  }

}