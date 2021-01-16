package feudalism.object;

import feudalism.FeudalismException;

@FunctionalInterface
public interface Lambda {
    abstract public void run() throws FeudalismException;
}
