package org.msweaver.groovy;

import org.springframework.core.io.ResourceLoader;
import org.springframework.scripting.ScriptSource;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.scripting.support.ScriptFactoryPostProcessor;
import org.springframework.scripting.support.StaticScriptSource;
import org.springframework.stereotype.Component;
import org.tiaa.groovy.GithubScriptSource;

@Component
public class CustomScriptFactoryPostProcessor extends ScriptFactoryPostProcessor {
    public static final String GITHUB_SCRIPT_PREFIX = "github:";

    @Override
    protected ScriptSource convertToScriptSource(String beanName,
                                                 String scriptSourceLocator,
                                                 ResourceLoader resourceLoader) {
        if (scriptSourceLocator.startsWith(INLINE_SCRIPT_PREFIX)) {
            return new StaticScriptSource(
                scriptSourceLocator.substring(INLINE_SCRIPT_PREFIX.length()), beanName);
        }
        else if (scriptSourceLocator.startsWith(GITHUB_SCRIPT_PREFIX)) {
            return new GithubScriptSource(
                scriptSourceLocator.substring(GITHUB_SCRIPT_PREFIX.length()));
        }
        else {
            return new ResourceScriptSource(
                resourceLoader.getResource(scriptSourceLocator));
        }
    }
}
