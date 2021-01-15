package feudalism.object;

import feudalism.FeudalismException;

@FunctionalInterface
public interface ConfirmationInterface {
    abstract public void onConfirm() throws FeudalismException;
}
