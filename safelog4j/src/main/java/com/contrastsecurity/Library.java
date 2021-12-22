package com.contrastsecurity;

import java.net.URL;

public class Library {
    public String name = null;
    public String version = null;
    public String url = null;
    public String path = null;
    public String filename = null;

    // jar:file:/Users/jeffwilliams/Downloads/log4j%20demo/myproject-0.0.1-SNAPSHOT.jar!/BOOT-INF/lib/log4j-core-2.14.1.jar!/
    // log4j-core-2.14.1.jar

    public static void main( String[] args ) {
        String url = "jar:file:/Users/jeffwilliams/Downloads/log4j%20demo/myproject-0.0.1-SNAPSHOT.jar!/BOOT-INF/lib/log4j-api-2.14.1.jar!/";
        Library lib = new Library( url );
        System.out.println( "LIB: " + lib );
    }

    public Library( URL url ) {
        this( url.getPath() );
    }

    public Library( String url ) {
        this.url = url;
        filename = url.substring(0,url.lastIndexOf(".jar") + 4);
        filename = filename.substring(filename.lastIndexOf("/") + 1 );
        name = filename.substring(0, filename.lastIndexOf('-') );
        version = filename.substring(filename.lastIndexOf('-') + 1, filename.lastIndexOf('.') );
    }

    public Library(String name, String version) {
        this.name = name;
        this.version = version;
    }

    public String toString() {
        return name + '-' + version + " | " + filename + " | " + url;
    }
}
