FROM quay.io/keycloak/keycloak:24.0.1

# Enable legacy theme support if needed
ENV KC_FEATURES=legacy

# Copy your custom theme to the Keycloak themes directory
COPY my-theme /opt/keycloak/themes/my-theme