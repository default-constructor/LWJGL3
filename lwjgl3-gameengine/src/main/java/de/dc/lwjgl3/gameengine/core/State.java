package de.dc.lwjgl3.gameengine.core;

public enum State {

	INTRO(0, "intro"), MAIN_MENU(1, "mainmenu"), GAME_PLAY(2, "gameplay"), GAME_MENU(3, "gamemenu");

	private int id;
	private String name;

	State(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}
}
