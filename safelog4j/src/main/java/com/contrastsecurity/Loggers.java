package com.contrastsecurity;

import java.util.Map;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class Loggers {
    public static Map<Class, Object> map = new HashMap<Class, Object>();

    public static void put( Object logger ) {
        Class clazz = logger.getClass();
        map.put( clazz, logger );
    }

    public static Object get( Class cl ) {
        return map.get( cl );
    }

	public static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");  

    public static void log( String msg ) {
        String stamp = formatter.format(new Date());
        String message = stamp + " TRACE --- [safelog4j] " + msg;
        System.out.println( message );
    }

    public static void log4j(String msg, Class cl) {
        String stamp = formatter.format(new Date());

        try {
            Object logger = map.get( cl );
            
            Class levelClass = Class.forName( "org.apache.logging.log4j.Level", true, cl.getClassLoader());
            Class markerClass = Class.forName( "org.apache.logging.log4j.Marker", true, cl.getClassLoader());
            Class messageClass = Class.forName("org.apache.logging.log4j.message.Message", true, cl.getClassLoader());
            Class simpleMessageClass = Class.forName("org.apache.logging.log4j.message.SimpleMessage", true, cl.getClassLoader());
            
            // make level
            Method levelMethod = levelClass.getMethod("getLevel", String.class );
            Object levelObject = levelMethod.invoke( null, "FATAL" );

            // make message
            Constructor simpleMessageCtor = simpleMessageClass.getConstructor(String.class);
            Object messageObject = simpleMessageCtor.newInstance(msg);
        
            // get log method -- log(Level level, Marker marker, String fqcn, StackTraceElement location, Message message, Throwable throwable) 
            Method log = logger.getClass().getDeclaredMethod("log", levelClass, markerClass, String.class, StackTraceElement.class, messageClass, Throwable.class );
            log.setAccessible(true);

            // send message to their log4j
            log.invoke( logger, levelObject, null, "log4j", null, messageObject, null );
        } catch( Exception e ) {
            log( "Sending message to log4j failed -- " + e.getMessage() );
            e.printStackTrace();
            log( "Attempted to log message: " + msg );
        }
    }
}