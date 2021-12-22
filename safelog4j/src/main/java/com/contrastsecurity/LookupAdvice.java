package com.contrastsecurity;

import net.bytebuddy.asm.Advice;

public class LookupAdvice {

    @Advice.OnMethodEnter( skipOn = String.class )
    public static String onEnter() {

        // is this a synthetic security test log message?
        if ( SafeLog4J.testScope.inScope() ) {
            SafeLog4J.log4ShellFound = true;
            return "attack blocked by safelog4j";
        }

        if ( SafeLog4J.blockMode ) {
            return "attack blocked by safelog4j";
        }

        // 'return null' causes ByteBuddy to execute original method body and return normally - see skipOn
        return null;
    }

    @Advice.OnMethodExit
    public static void onExit( @Advice.Enter String enter, @Advice.Return(readOnly = false) String ret) {
    }

}