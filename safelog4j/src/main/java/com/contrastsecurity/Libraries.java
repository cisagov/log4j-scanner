package com.contrastsecurity;

import java.net.URL;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public class Libraries {
   
    private static Map<String, Library> map = new TreeMap<String, Library>();

    public static void add(URL url) {
        try {
            Library lib = new Library( url );
            map.put( url.getPath(), lib );
        } catch( Exception e ) {
            Loggers.log( "Couldn't parse library information from CodeSource." );
            Loggers.log( "Analysis and protection are still effective." );
            Loggers.log( "Please submit an issue with the full URL below." );
            Loggers.log( "https://github.com/Contrast-Security-OSS/safelog4j/issues/new/choose" );
            Loggers.log( "URL: " + url );
        }
    }

    public static Collection<Library> getLibraries() {
        return map.values();
    }

    public static void dump() {
        for ( Library lib : map.values() ) {
            Loggers.log( lib.toString() );
        }
    }
    
}
