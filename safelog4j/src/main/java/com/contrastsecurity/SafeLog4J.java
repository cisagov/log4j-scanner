package com.contrastsecurity;

import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.AgentBuilder.InitializationStrategy;
import net.bytebuddy.agent.builder.AgentBuilder.RedefinitionStrategy;
import net.bytebuddy.agent.builder.AgentBuilder.TypeStrategy;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.matcher.StringMatcher;
import net.bytebuddy.utility.JavaModule;

import static net.bytebuddy.matcher.ElementMatchers.*;

public class SafeLog4J {

	public static BinaryScope logScope = new BinaryScope("log");
	public static BinaryScope testScope = new BinaryScope("test");

	public static boolean blockMode = true;
	public static boolean checkMode = true;
	public static boolean agentRunning = false;
	public static boolean log4jTested = false;
	public static boolean log4jFound = false;
	public static boolean log4ShellFound = false;

	public static void premain(String args, Instrumentation inst) {
		transform( args, inst );
	}

	public static void agentmain(String args, Instrumentation inst) {
		transform( args, inst );
	}

	public static void transform(String args, Instrumentation inst) {
		if ( agentRunning ) {
			Loggers.log( "Already running? Check for multiple -javaagent declarations" );
			return;
		}
		agentRunning = true;

		if ( args == null ) args = "both";
		switch(args.toLowerCase()) {
			case "block" : checkMode = false; break;
			case "check" : blockMode = false; break;
			case "none"  : return;
		}

		Loggers.log( "SafeLog4J by Contrast Security" );
		Loggers.log( "https://contrastsecurity.com" );
		Loggers.log( "" );
		Loggers.log( "Instrumentation-based help with finding and fixing log4shell" );
		Loggers.log( "Usage: -javaagent:safelog4j.jar         -- enable both check and block" );
		Loggers.log( "     : -javaagent:safelog4j.jar=check   -- check for log4j exploitability" );
		Loggers.log( "     : -javaagent:safelog4j.jar=block   -- block log4j exploits from succeeding" );
		Loggers.log( "     : -javaagent:safelog4j.jar=none    -- disable both check and block" );
		Loggers.log( "" );
		Loggers.log( "Check mode: " + ( checkMode ? "enabled" : "disabled" ) );
		Loggers.log( "Block mode: " + ( blockMode ? "enabled" : "disabled" ) );
		Loggers.log( "" );
		Loggers.log( "SafeLog4J analyzes and protects all log4j instances across classloaders" );
		Loggers.log( "" );

		new AgentBuilder.Default()
		// .with(AgentBuilder.Listener.StreamWriting.toSystemError().withTransformationsOnly())
		// .with(AgentBuilder.Listener.StreamWriting.toSystemError().withErrorsOnly())
		.with(new InstListener(new StringMatcher(".log4j.core.lookup.JndiLookup", StringMatcher.Mode.ENDS_WITH)))
		.with(RedefinitionStrategy.RETRANSFORMATION)
		.with(InitializationStrategy.NoOp.INSTANCE)
		.with(TypeStrategy.Default.REDEFINE)
		.disableClassFormatChanges()

		.type(nameEndsWith(".log4j.core.Logger"))
		.transform(new AgentBuilder.Transformer.ForAdvice()
			.include(ClassLoader.getSystemClassLoader(), inst.getClass().getClassLoader())
			.advice(isMethod(), LogAdvice.class.getName()))

		.type(nameEndsWith(".log4j.core.lookup.JndiLookup"))
        .transform(new AgentBuilder.Transformer.ForAdvice()
			.include(ClassLoader.getSystemClassLoader(), inst.getClass().getClassLoader())
			.advice(isMethod(), LookupAdvice.class.getName()))

		.type(new AgentBuilder.RawMatcher() {
			@Override
			public boolean matches(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, Class<?> classBeingRedefined, ProtectionDomain protectionDomain) {
				if ( typeDescription.getCanonicalName().contains( ".log4j.")) {
					Libraries.add( protectionDomain.getCodeSource().getLocation());
				}
				return false;
			}
		})
		.transform(new AgentBuilder.Transformer() {
			@Override
			public Builder<?> transform(Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module) {
				return null;
			}
		})
		
		.installOn(inst);
		

		new BOMvoyage(inst).run();
	}

}
