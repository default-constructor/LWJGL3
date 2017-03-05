package de.dc.lwjgl3.gameengine.game;

import de.dc.lwjgl3.gameengine.core.State;

public abstract class GameState {

	protected State state;
	protected State nextState;

	protected boolean stateChanged;

	public GameState(State state) {
//		System.out.println("GameState::constructor");
		this.state = state;
	}

	public abstract void destroy();

	public State getNextState() {
//		System.out.println("GameState::getNextState");
		return nextState;
	}

	public boolean hasStateChanged() {
//		System.out.println("GameState::hasStateChanged");
		return stateChanged;
	}

	public abstract void init();

	public abstract void render();

	public abstract void update();
}
