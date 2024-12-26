package com.hyc.nav_annotation;

public @interface Destination {
    String pageUrl();
    boolean asStarter() default false;
}
