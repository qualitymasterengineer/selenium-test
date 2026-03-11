package com.saucedemo.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Central configuration: base URL and credentials from config.properties or environment variables.
 */
public final class Config {

    private static final String CONFIG_FILE = "config.properties";
    private static Properties props;

    static {
        props = new Properties();
        try (InputStream is = Config.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (is != null) {
                props.load(is);
            }
        } catch (IOException e) {
            // use defaults
        }
    }

    private Config() {}

    public static String getBaseUrl() {
        return get("base.url", "BASE_URL", "https://www.saucedemo.com");
    }

    public static String getDefaultUsername() {
        return get("default.username", "DEFAULT_USERNAME", "standard_user");
    }

    public static String getDefaultPassword() {
        return get("default.password", "DEFAULT_PASSWORD", "secret_sauce");
    }

    public static int getImplicitWaitMs() {
        return Integer.parseInt(get("implicit.wait.ms", "IMPLICIT_WAIT_MS", "5000"));
    }

    public static int getExplicitWaitTimeoutSec() {
        return Integer.parseInt(get("explicit.wait.timeout.sec", "EXPLICIT_WAIT_TIMEOUT_SEC", "10"));
    }

    public static int getPageLoadTimeoutSec() {
        return Integer.parseInt(get("page.load.timeout.sec", "PAGE_LOAD_TIMEOUT_SEC", "30"));
    }

    private static String get(String propKey, String envKey, String defaultValue) {
        String env = System.getenv(envKey);
        if (env != null && !env.isBlank()) {
            return env.trim();
        }
        return props.getProperty(propKey, defaultValue);
    }
}
