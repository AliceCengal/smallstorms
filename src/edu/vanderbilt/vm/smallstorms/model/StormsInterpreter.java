package edu.vanderbilt.vm.smallstorms.model;

import edu.vanderbilt.vm.smallstorms.util.VirtualStorms;

/**
 * Date: 7/10/13
 * Time: 5:32 AM
 */
public class StormsInterpreter {

/**
 * @return a new instance of StormsInterpreter
 */
public StormsInterpreter newInstance() {
    return new StormsInterpreter();
}

protected StormsInterpreter() {}

/**
 * Set the virtual machine to be used as target.
 *
 * @param vm target Bot
 * @return this instance of StormsInterpreter for call chaining
 */
public StormsInterpreter using(VirtualStorms vm) { return this; }

/**
 * @param callback Callback to receive operational info from the Interpreter.
 * @return this instance of StormsInterpreter for call chaining
 */
public StormsInterpreter setCallback(StormsCallback callback) { return this; }

/**
 * Instruct the interpreter to start running the program passed in the argument.
 */
public void interpret() {  }

/**
 * @return Interpreter's status
 */
public StormsStatus getStatus() { return null; }

/**
 * Reset the Interpreter's state. May be better to just create another instance instead of
 * reusing this instance.
 *
 * @return this instance of StormsInterpreter for call chaining
 */
public StormsInterpreter reset() { return this; }

/**
 * An interface for receiving callback from the Interpreter.
 */
public interface StormsCallback {
    void onFinish(StormsStatus status);
    void onError(StormsStatus status);
}

/**
 * An interface for accessing the current state of the Interpreter. Passed as argument in the callback methods.
 */
public interface StormsStatus {

    boolean isFinish();
    boolean isError();
    int getErrorCode();
    String getCurrentBlockId();
    double getRunTime();

}



}
