package br.edu.ufersa.ed1.cercoDePapel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MenuScreen implements Screen {
    private final Main game;
    private Stage stage;
    private BitmapFont fontTitle;
    private BitmapFont fontButton;
    private Texture buttonTexture;

    public MenuScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        createFonts();
        createButtonTexture();

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        // --- ESTILOS ---
        Label.LabelStyle titleStyle = new Label.LabelStyle(fontTitle, Color.WHITE);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = fontButton;
        buttonStyle.up = new TextureRegionDrawable(buttonTexture);
        buttonStyle.down = new TextureRegionDrawable(buttonTexture);
        buttonStyle.fontColor = Color.BLACK;
        buttonStyle.downFontColor = Color.DARK_GRAY;

        // --- COMPONENTES ---
        Label titleLabel = new Label("CERCO DE PAPEL", titleStyle);

        TextButton playButton = new TextButton("JOGAR", buttonStyle);
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // ATUALIZADO: Troca para a tela do jogo e descarta o menu para liberar memória
                game.setScreen(new GameScreen(game));
                dispose();
            }
        });

        TextButton exitButton = new TextButton("SAIR", buttonStyle);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        // --- MONTAGEM ---
        table.add(titleLabel).padBottom(50).row();
        table.add(playButton).width(200).height(60).padBottom(20).row();
        table.add(exitButton).width(200).height(60).row();
    }

    private void createFonts() {
        // CORREÇÃO: Nome exato do arquivo (case sensitive)
        if (Gdx.files.internal("Roboto-Black.ttf").exists()) {
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Roboto-Black.ttf"));
            FreeTypeFontParameter parameter = new FreeTypeFontParameter();

            parameter.size = 50;
            parameter.borderWidth = 2;
            parameter.borderColor = Color.DARK_GRAY;
            parameter.shadowOffsetX = 3;
            parameter.shadowOffsetY = 3;
            parameter.minFilter = Texture.TextureFilter.Linear;
            parameter.magFilter = Texture.TextureFilter.Linear;
            fontTitle = generator.generateFont(parameter);

            parameter.size = 24;
            parameter.borderWidth = 0;
            parameter.shadowOffsetX = 0;
            parameter.shadowOffsetY = 0;
            fontButton = generator.generateFont(parameter);

            generator.dispose();
        } else {
            System.err.println("AVISO: 'Roboto-Black.ttf' não encontrado na pasta assets! Usando fonte padrão.");
            fontTitle = new BitmapFont();
            fontTitle.getData().setScale(3);
            fontTitle.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

            fontButton = new BitmapFont();
            fontButton.getData().setScale(2);
            fontButton.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
    }

    private void createButtonTexture() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.LIGHT_GRAY);
        pixmap.fill();
        buttonTexture = new Texture(pixmap);
        pixmap.dispose();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        if (stage != null) stage.dispose();
        if (fontTitle != null) fontTitle.dispose();
        if (fontButton != null) fontButton.dispose();
        if (buttonTexture != null) buttonTexture.dispose();
    }
}
