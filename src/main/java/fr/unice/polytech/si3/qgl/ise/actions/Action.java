package fr.unice.polytech.si3.qgl.ise.actions;

public abstract class Action
{
    private boolean isFinished = false;

    public boolean isFinished ()
    {
        return isFinished;
    }

    protected void finish ()
    {
        isFinished = true;
    }

    public void reset ()
    {
        isFinished = false;
    }

    public abstract String apply ();
}
