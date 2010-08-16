package battleship.view;

import java.util.Stack;

public class TestUserInterfaceSpy implements UserInterface {

    private final Stack<String> inputsStack = new Stack<String>();
    private final Stack<String> outputsStack = new Stack<String>();

    @Override
    public String in() {
        return inputsStack.pop();
    }

    @Override
    public void out(final String output) {
        outputsStack.push(output);
    }

    public void setNextInput(final String mockedInput) {
        inputsStack.push(mockedInput);
    }

    public String getLastOutput() {
        return outputsStack.pop();
    }
}
