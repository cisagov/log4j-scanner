package com.contrastsecurity;

import java.io.File;
import java.util.List;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

import net.bytebuddy.agent.ByteBuddyAgent;

public class App {

    public static void main(String[] args){
        System.out.println();
        System.out.println("SafeLog4j by Contrast Security");
        System.out.println( "https://contrastsecurity.com" );

        if ( args.length > 0 && args.length < 3 ) {
            try{
                String pid = args[0];
                String options = args.length>=2 ? args[1] : null;

                String filename = App.class.getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI()
                    .getPath();
                File agentFile = new File(filename);
                ByteBuddyAgent.attach(agentFile.getAbsoluteFile(), pid, options);
                System.out.println("Attached to target jvm and loaded agent successfully");
                System.out.println();
            }catch(Exception e){
                e.printStackTrace();
            }
        } else {
            showHelp();
        }
    }

    private static void showHelp(){
        System.out.println();
        System.out.println("List of eligible JVM PIDs (must be running as same user):");
        try{
            listProcesses();
        }catch(NoClassDefFoundError err){
            System.err.println("Error. Try using 'jps' or 'jcmd' to list Java processes.");
        }
        System.out.println();
        System.out.println("To attach Safelog4j to your application, either:");
        System.out.println("1. Launch with -javaagent:safelog4j-x.x.x");
        System.out.println("2. Attach to a running JVM with java -jar safelog4j-x.x.x PID [both|check|block|none]");
        System.out.println();
    }

    public static void listProcesses(){
        List<VirtualMachineDescriptor> vms = VirtualMachine.list();
        vms.stream()
            .filter(vm -> !vm.displayName().contains("safelog")) //No need to patch ourselves
            .forEach(vm -> {
            System.out.println(vm.id() + " \t" + vm.displayName());
        });
    }

}
