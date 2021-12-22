package com.contrastsecurity;

public final class BinaryScope {

	private String name = "scope";

	private final ThreadLocal<Counter> counter = new ThreadLocal<Counter>() {
		@Override
		protected Counter initialValue() {
			return new Counter();
		}
	};

	public BinaryScope(String name) {
		this.name = name;
    }

    public boolean inScope() {
		return counter.get().value != 0;
	}

	public boolean inOutermostScope() {
		return counter.get().value == 1;
	}

	public boolean inNestedScope() {
		return counter.get().value > 1;
	}

	public void enterScope() {
		counter.get().value++;
		// Loggers.log( "ENTERED SCOPE: " + this );
	}

	public void leaveScope() {
		counter.get().value--;
		// Loggers.log( "EXITED SCOPE: " + this );
	}

	@Override
	public String toString() {
		return name + "-" + counter.get().value;
	}

	private static final class Counter {
		private int value;
	}
}