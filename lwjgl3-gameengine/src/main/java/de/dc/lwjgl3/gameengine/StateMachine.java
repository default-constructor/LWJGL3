package de.dc.lwjgl3.gameengine;

import de.dc.lwjgl3.gameengine.core.State;
import de.dc.lwjgl3.gameengine.game.GameMenu;
import de.dc.lwjgl3.gameengine.game.GamePlay;
import de.dc.lwjgl3.gameengine.game.GameState;
import de.dc.lwjgl3.gameengine.game.Intro;
import de.dc.lwjgl3.gameengine.game.MainMenu;

public class StateMachine {

	private GameState[] states;

	private State currentState;

	public StateMachine() {
//		System.out.println("StateMachine::constructor");
		states = new GameState[] {
				new Intro(), new MainMenu(), new GamePlay(), new GameMenu()
		};
	}

	public GameState getState() {
//		System.out.println("StateMachine::getState");
		return states[currentState.getId()];
	}

	public void setState(State state) {
//		System.out.println("StateMachine::setState");
		currentState = state;
	}
}
