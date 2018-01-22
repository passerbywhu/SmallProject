package com.passerbywhu.smallproject.dagger;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;

/**
 * Created by passe on 2017/9/7.
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface SmallQualifier {
}
