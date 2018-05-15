package fr.unice.polytech.si3.qgl.ise.actions;

public abstract class Action {
    private boolean isFinished = false;

    /**
     * Allow you to do a kind of loop with this function as condition
     *
     * @return <code>true</code> if the action is done <code>false</code> otherwise
     */
    public boolean isFinished() {
        return isFinished;
    }

    /**
     * Use to reperform the action
     */
    public void reset() {
        isFinished = false;
    }

    /**
     * Main function, to perform everything needed in the action
     *
     * @return JSON formatted String to do the action
     */
    public abstract String apply();

    /**
     * Finish the action
     */
    protected void finish() {
        isFinished = true;
    }
}
