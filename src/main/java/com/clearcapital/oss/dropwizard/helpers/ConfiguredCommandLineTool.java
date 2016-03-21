package com.clearcapital.oss.dropwizard.helpers;

import io.dropwizard.Configuration;
import io.dropwizard.cli.ConfiguredCommand;
import io.dropwizard.setup.Bootstrap;
import net.sourceforge.argparse4j.inf.Namespace;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.clearcapital.oss.slf4j.Slf4jHelpers;

/**
 * A class to help build command line tools for DropWizard. Importantly, this class allows specifying the System.exit()
 * status, which is useful when writing shell scripts which call your DropWizard app.
 * 
 * @param <T>
 */
public abstract class ConfiguredCommandLineTool<T extends Configuration> extends ConfiguredCommand<T> {

    private static final Logger log = LoggerFactory.getLogger(ConfiguredCommandLineTool.class);

    protected ConfiguredCommandLineTool(final String name, final String description) {
        super(name, description);
    }

    @Override
    final protected void run(final Bootstrap<T> bootstrap, final Namespace namespace, final T configuration)
            throws Exception {
        int status = -1;
        Slf4jHelpers.addShutdownHook();
        try {
            status = execute(bootstrap, namespace, configuration);
        } catch (Exception e) {
            log.error("Caught exception during execution:", e);
        } finally {
            log.debug("Shutting Down with status:" + status);

            // this is ugly, but DropWizard doesn't share its jetty server, so System.exit is the only
            // way to force a shutdown. Plus, we get system exit codes!
            cleanup();
            System.runFinalization();
            System.exit(status);
        }
    }

    abstract protected int execute(Bootstrap<T> bootstrap, Namespace namespace, T configuration) throws Exception;

}
