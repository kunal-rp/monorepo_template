package com.util;

import com.google.inject.AbstractModule;
import com.user.db.UserDBHandler;
import com.user.db.MainUserDBHandler;
import com.user.util.SSOValidator;
import com.user.util.MainSSOValidator;

public class ServiceModule extends AbstractModule{

    @Override
    protected void configure() {
        bind(UserDBHandler.class).to(MainUserDBHandler.class);
        bind(SSOValidator.class).to(MainSSOValidator.class);
    }
}