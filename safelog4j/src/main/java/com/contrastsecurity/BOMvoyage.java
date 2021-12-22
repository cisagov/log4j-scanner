package com.contrastsecurity;

import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.Instrumentation;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.stream.Collectors;

import org.cyclonedx.generators.json.BomJsonGenerator13;
import org.cyclonedx.model.Bom;
import org.cyclonedx.model.Component;
import org.cyclonedx.model.Dependency;
import org.cyclonedx.model.Property;

public class BOMvoyage {

    private final Instrumentation inst;

    BOMvoyage(Instrumentation inst){
        this.inst=inst;
    }

    private Bom bom = new Bom();

    private Map<String, Component> dedup = new HashMap<>();

    void run(){
        List<URLHolder> files = Arrays.stream(inst.getAllLoadedClasses())
            .map(clazz -> clazz.getProtectionDomain())
            .filter(Objects::nonNull)
            .map(pd -> new CodeSourceHolder(pd.getCodeSource(), pd.getClassLoader()))
            .filter(csh -> csh.codeSource!=null)
            .map(csh -> new URLHolder(csh.codeSource.getLocation(), csh.loader))
            .distinct()
            .collect(Collectors.toList());

        files.forEach(file -> parse(file));

        BomJsonGenerator13 generator = new BomJsonGenerator13(bom);
        String out = generator.toJsonString();
        Loggers.log(out);
    }

    void parse(URLHolder file){
        parse(file, file.url.toString());
    }

    Component parse(URLHolder fullPath, String currentName){
        int end = currentName.lastIndexOf("!/");
        if(end==-1){
            Component r = dedup.get(currentName);
            if(r==null){
                r = new Component();
                r.setName(currentName);
                dedup.put(currentName, r);
                bom.addComponent(r);
                readManifestFor(fullPath, r);
            }
            return r;
        }else{
            String parentName = currentName.substring(0, end);
            Component parent = parse(fullPath, parentName);
            String fileName = currentName.substring(end);
            if(!"!/".equals(fileName)){
                Component self = dedup.get(currentName);
                if(self==null){
                    self = new Component();
                    self.setName(fileName);
                    Property filename = new Property();
                    filename.setName("Filename");
                    filename.setValue(currentName);
                    self.setProperties(Arrays.asList(filename));
                    parent.addComponent(self);
                    readManifestFor(fullPath, self);
                    dedup.put(currentName, self);
                }
                return self;
            }
            return null;
        }
    }

    private void readManifestFor(URLHolder h, Component c){
        String loc = h.url.toExternalForm();
        if(loc.startsWith("jar:file")){
            String readLoc = loc + "META-INF/MANIFEST.MF";
            try{
                URL newURL = new URL(readLoc);
                try(InputStream in = newURL.openStream()){
                    Manifest manifest = new Manifest(in);
                    Attributes attributes = manifest.getMainAttributes();
                    String title = attributes.getValue("Implementation-Title");
                    c.setAuthor(title);
                    String version = attributes.getValue("Implementation-Version");
                    c.setVersion(version);
                }
            }catch(IOException|NullPointerException ex){
                Loggers.log("Unable to read MANIFEST of " + readLoc);
            }
        }
    }

    private static class CodeSourceHolder{
        CodeSource codeSource;
        ClassLoader loader;

        CodeSourceHolder(CodeSource source, ClassLoader loader){
            this.codeSource=source;
            this.loader=loader;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 41 * hash + Objects.hashCode(this.codeSource);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final CodeSourceHolder other = (CodeSourceHolder) obj;
            if (!Objects.equals(this.codeSource, other.codeSource)) {
                return false;
            }
            return true;
        }
    }

    private static class URLHolder{
        URL url;
        ClassLoader loader;

        URLHolder(URL url, ClassLoader loader){
            this.url=url;
            this.loader=loader;
        }

        @Override
        public int hashCode() {
            int hash = 9;
            hash = 41 * hash + Objects.hashCode(this.url);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final URLHolder other = (URLHolder) obj;
            if (!Objects.equals(this.url, other.url)) {
                return false;
            }
            return true;
        }
    }
}
