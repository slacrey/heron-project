package com.loadburn.heron.webapp;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.google.inject.servlet.GuiceServletContextListener;
import com.loadburn.heron.HeronModule;
import com.loadburn.heron.core.HeronServletModule;
import com.loadburn.heron.email.EmailConfigLoaderPlugin;
import com.loadburn.heron.email.HeronEmailModule;
import com.loadburn.heron.plugin.Plugin;
import com.loadburn.heron.plugin.PluginModule;
import com.loadburn.heron.plugin.ScanWebPackagePlugin;
import com.loadburn.heron.storage.StorageFilter;
import com.loadburn.heron.storage.sql.mysql.MySQLStorageModule;
import com.loadburn.heron.webapp.web.HelloWorld;
import com.loadburn.heron.webapp.web.Person;

import java.util.Set;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-10-24
 */
public class HeronConfig extends GuiceServletContextListener {

    private Injector injector;

    protected Injector getInjector() {

        injector = Guice.createInjector(Stage.DEVELOPMENT,
                new HeronModule() {
                    @Override
                    protected void configureHeron() {
                        scanWeb(HelloWorld.class.getPackage());
                        install(new HeronEmailModule());
                        install(new PluginModule() {

                            @Override
                            protected void configureAware(Set<Class<? extends Plugin>> classAware) {
                                classAware.add(ScanWebPackagePlugin.class);
                                classAware.add(EmailConfigLoaderPlugin.class);
                            }
                        });
                        install(new MySQLStorageModule("storage.json") {
                            @Override
                            protected void configureEntity() {
                                scanEntity(Person.class.getPackage());
                            }
                        });
                    }

                    @Override
                    protected HeronServletModule configureHeronServletModule() {
                        return new HeronServletModule(){
                            @Override
                            protected void configureBeforeFilter() {
                                filter("/*").through(StorageFilter.class);
                            }
                        };
                    }
                }
        );
        return injector;
    }
}
