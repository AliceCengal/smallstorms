package edu.vanderbilt.vm.smallstorms.framework;

public abstract class Screen {
    protected final Game mGame;

    public Screen(Game game) {
        this.mGame = game;
    }

    public abstract void update(float deltaTime);

    public abstract void present(float deltaTime);

    public abstract void pause();

    public abstract void resume();

    public abstract void dispose();
}
