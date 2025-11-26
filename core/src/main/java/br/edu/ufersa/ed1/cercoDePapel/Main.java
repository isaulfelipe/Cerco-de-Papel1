package br.edu.ufersa.ed1.cercoDePapel;

import com.badlogic.gdx.Game;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    @Override
    public void create() {
        // Inicia diretamente na tela de Menu
        setScreen(new MenuScreen(this));
    }
}
