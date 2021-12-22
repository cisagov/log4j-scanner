
	package com.contrastsecurity;
    
    import net.bytebuddy.agent.builder.AgentBuilder;
    import net.bytebuddy.description.type.TypeDescription;
    import net.bytebuddy.dynamic.DynamicType;
    import net.bytebuddy.matcher.ElementMatcher;
    import net.bytebuddy.utility.JavaModule;

    public class InstListener implements AgentBuilder.Listener {
		private final ElementMatcher<? super String> matcher;

		public InstListener(ElementMatcher<? super String> matcher) {
			this.matcher = matcher;
		}

		public void onTransformation(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, boolean loaded, DynamicType dynamicType) {
			if ( matcher.matches(typeDescription.getName()) ) {
				SafeLog4J.log4jFound = true;
			}
		}

		@Override
		public void onDiscovery(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {			
		}

		@Override
		public void onIgnored(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, boolean loaded) {
		}

		@Override
		public void onError(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded, Throwable throwable) {
		}

		@Override
		public void onComplete(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {
		}
	}
