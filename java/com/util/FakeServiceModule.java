package com.util;

import com.google.inject.AbstractModule;
import com.user.db.UserDBHandler;
import com.user.db.FakeUserDBHandler;
import com.user.util.SSOValidator;
import com.user.util.FakeSSOValidator;

public class FakeServiceModule extends AbstractModule{

    @Override
    protected void configure() {
        bind(UserDBHandler.class).to(FakeUserDBHandler.class);
        bind(SSOValidator.class).to(FakeSSOValidator.class);
    }
}