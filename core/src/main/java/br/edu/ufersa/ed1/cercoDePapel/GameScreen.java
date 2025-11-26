package br.edu.ufersa.ed1.cercoDePapel;

import br.edu.ufersa.ed1.cercoDePapel.entities.SpellCard;
import br.edu.ufersa.ed1.cercoDePapel.util.MyLinkedList;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import br.edu.ufersa.ed1.cercoDePapel.entities.BoardUnit;
import br.edu.ufersa.ed1.cercoDePapel.entities.Card;
import br.edu.ufersa.ed1.cercoDePapel.entities.UnitCard;
import br.edu.ufersa.ed1.cercoDePapel.logic.BattleManager;
import br.edu.ufersa.ed1.cercoDePapel.util.FileController;

import java.util.Set;

public class GameScreen implements Screen {
    private final Main game;

    // --- DIMENSÕES VIRTUAIS ---
    private static final float VIRTUAL_WIDTH = 2000;
    private static final float MAP_HEIGHT = 1400;
    private static final float HUD_HEIGHT = 350;
    private static final float VIRTUAL_HEIGHT = MAP_HEIGHT + HUD_HEIGHT;

    // --- MUNDO DO JOGO ---
    private OrthographicCamera gameCamera;
    private Viewport gameViewport;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private SpriteBatch batch;

    // --- FONTES ---
    private BitmapFont font;         // Para HP das unidades
    private BitmapFont hudFont;      // Para texto do HUD (Mana, Turno)
    private BitmapFont cardFont;     // Para texto dentro das cartas

    // --- ASSETS ---
    private Texture characterTexture;
    private Texture selectionTexture;

    // --- LÓGICA ---
    private BattleManager battleManager;
    private BoardUnit selectedUnit; // Unidade no mapa selecionada
    private Card selectedCard;     // Carta na mão selecionada

    private Vector3 touchPoint = new Vector3();
    private final int TILE_SIZE = 200;

    // --- HUD (INTERFACE) ---
    private Viewport hudViewport;
    private Stage hudStage;
    private Texture deckTexture;
    private Texture cardTexture;
    private Label manaLabel;
    private Label turnLabel;

    // Placeholders visuais para o deck (apenas decorativo, a mão é desenhada via código)
    private Image deckImage;

    // Configuração visual das cartas na mão
    private final float CARD_WIDTH = 200;
    private final float CARD_HEIGHT = 280;
    private final float HAND_Y = 35;

    public GameScreen(Main game) {
        this.game = game;
        // O BattleManager já inicializa o Deck e a Mão usando ListaEncadeada internamente
        this.battleManager = new BattleManager();
    }

    @Override
    public void show() {
        // 1. CÂMERA E MAPA
        gameCamera = new OrthographicCamera();
        gameViewport = new FitViewport(VIRTUAL_WIDTH, MAP_HEIGHT, gameCamera);

        try {
            map = new TmxMapLoader().load("maps/testMap.tmx");
            mapRenderer = new OrthogonalTiledMapRenderer(map);
        } catch (Exception e) {
            System.err.println("Erro ao carregar mapa: " + e.getMessage());
        }

        gameCamera.position.set(VIRTUAL_WIDTH / 2, MAP_HEIGHT / 2, 0);

        batch = new SpriteBatch();

        // Configuração de fontes
        font = new BitmapFont();
        font.getData().setScale(3);
        font.setColor(Color.RED);

        hudFont = new BitmapFont();
        hudFont.getData().setScale(4);
        hudFont.setColor(Color.WHITE);

        cardFont = new BitmapFont();
        cardFont.getData().setScale(2);
        cardFont.setColor(Color.BLACK);

        characterTexture = new Texture("objects/Character.png");

        // Inicializa inimigos de teste
        try {
            Set<UnitCard> allUnits = FileController.readAllUnits();
            if (!allUnits.isEmpty()) {
                // Adiciona um inimigo para testar (Arqueiro)
                UnitCard archer = allUnits.stream().filter(u -> u.name.equals("Arqueiro")).findFirst().orElse(allUnits.iterator().next());
                battleManager.addUnit(new BoardUnit(archer, 6, 3, false));
            }
        } catch (Exception e) { e.printStackTrace(); }

        // Cria textura de seleção (quadrado verde)
        Pixmap pix = new Pixmap(TILE_SIZE, TILE_SIZE, Pixmap.Format.RGBA8888);
        pix.setColor(0, 1, 0, 0.5f);
        int border = 10;
        pix.fillRectangle(0, 0, TILE_SIZE, border);
        pix.fillRectangle(0, 0, border, TILE_SIZE);
        pix.fillRectangle(TILE_SIZE - border, 0, border, TILE_SIZE);
        pix.fillRectangle(0, TILE_SIZE - border, TILE_SIZE, border);
        selectionTexture = new Texture(pix);
        pix.dispose();

        // 2. HUD CONFIGURAÇÃO
        hudViewport = new FitViewport(VIRTUAL_WIDTH, HUD_HEIGHT);
        hudStage = new Stage(hudViewport);

        deckTexture = new Texture("objects/Deck Placeholder.png");
        cardTexture = new Texture("objects/Card Placeholder.png");

        // Imagem do Deck (decorativa no canto esquerdo)
        deckImage = new Image(deckTexture);
        float margin = 20f;
        float availableH = HUD_HEIGHT - (margin * 2);
        float scale = availableH / cardTexture.getHeight();
        deckImage.setScale(scale);
        deckImage.setPosition(50, (HUD_HEIGHT - deckImage.getHeight() * scale) / 2);
        hudStage.addActor(deckImage);

        // Labels
        Label.LabelStyle labelStyle = new Label.LabelStyle(hudFont, Color.WHITE);
        manaLabel = new Label("Mana: 0/0", labelStyle);
        manaLabel.setPosition(30, 250);
        hudStage.addActor(manaLabel);

        turnLabel = new Label("Turno", labelStyle);
        turnLabel.setPosition(30, 150);
        hudStage.addActor(turnLabel);

        // 3. INPUT PROCESSOR
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                // Primeiro verifica clique no HUD (Mão)
                hudViewport.unproject(touchPoint.set(screenX, screenY, 0));
                if (touchPoint.y < HUD_HEIGHT) {
                    handleClickOnHand(touchPoint.x, touchPoint.y);
                    return true;
                }

                // Se não foi HUD, verifica Mapa
                gameViewport.unproject(touchPoint.set(screenX, screenY, 0));
                if (touchPoint.y > 0 && touchPoint.y < MAP_HEIGHT) {
                    int gridX = (int) (touchPoint.x / TILE_SIZE);
                    int gridY = (int) (touchPoint.y / TILE_SIZE);
                    handleClickOnBoard(gridX, gridY);
                    return true;
                }

                return false;
            }

            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.SPACE) {
                    battleManager.endTurn();
                    selectedCard = null;
                    selectedUnit = null;
                    return true;
                }
                return false;
            }
        });
    }

    // --- CLIQUE NA MÃO (USANDO LISTA ENCADEADA) ---
    private void handleClickOnHand(float x, float y) {
        // Obtém a lista personalizada
        MyLinkedList<Card> hand = battleManager.getPlayerHand();

        float startX = 400;
        float spacing = 220;

        // Percorre usando o método .pegar(i) da sua lista encadeada
        for (int i = 0; i < hand.size(); i++) {
            float cardX = startX + (i * spacing);

            // Verifica colisão com a carta visual
            if (x >= cardX && x <= cardX + CARD_WIDTH && y >= HAND_Y && y <= HAND_Y + CARD_HEIGHT) {
                Card clicked = hand.get(i); // Acesso via estrutura de dados

                if (selectedCard == clicked) {
                    selectedCard = null;
                    System.out.println("Carta deselecionada.");
                } else {
                    selectedCard = clicked;
                    selectedUnit = null;
                    System.out.println("Carta selecionada: " + clicked.name);
                }
                return;
            }
        }
        selectedCard = null;
    }

    // --- CLIQUE NO TABULEIRO ---
    private void handleClickOnBoard(int x, int y) {
        BoardUnit unitClicked = battleManager.getUnitAt(x, y);

        // Cenário A: Carta selecionada -> Tentar INVOCAR
        if (selectedCard != null) {
            if (selectedCard instanceof UnitCard) {
                boolean success = battleManager.tryPlayUnitCard((UnitCard)selectedCard, x, y);
                if (success) {
                    selectedCard = null; // Sucesso, carta usada e removida da lista
                }
            }
            else if (selectedCard instanceof SpellCard) {
                boolean success = battleManager.tryPlaySpellCard((SpellCard) selectedCard, x, y);
                if (success) {
                    selectedCard = null; // Sucesso, carta usada e removida da lista
                }
            }
            return;
        }

        // Cenário B: Lógica normal (Selecionar / Mover / Atacar)
        if (selectedUnit == null) {
            if (unitClicked != null && unitClicked.isPlayer()) {
                if (!unitClicked.hasMoved() && !unitClicked.hasAttacked()) {
                    selectedUnit = unitClicked;
                }
            }
        } else {
            if (unitClicked == selectedUnit) {
                selectedUnit = null;
            } else if (unitClicked != null && !unitClicked.isPlayer()) {
                boolean attacked = battleManager.tryAttack(selectedUnit, unitClicked);
                if (attacked) selectedUnit = null;
            } else if (unitClicked == null) {
                boolean moved = battleManager.tryMove(selectedUnit, x, y);
                if (moved) selectedUnit = null;
            } else if (unitClicked != null && unitClicked.isPlayer()) {
                selectedUnit = unitClicked;
            }
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        updateHudInfo();

        // 1. RENDERIZAÇÃO DO MAPA
        if (mapRenderer != null) {
            gameViewport.apply();
            mapRenderer.setView(gameCamera);
            mapRenderer.render();
        }

        // 2. RENDERIZAÇÃO DAS UNIDADES
        if (batch != null) {
            gameViewport.apply();
            batch.setProjectionMatrix(gameCamera.combined);
            batch.begin();
            for (BoardUnit unit : battleManager.getUnits()) {
                float drawX = unit.getGridX() * TILE_SIZE;
                float drawY = unit.getGridY() * TILE_SIZE;

                if (unit.hasAttacked() || unit.hasMoved()) batch.setColor(0.5f, 0.5f, 0.5f, 1f);
                else if (!unit.isPlayer()) batch.setColor(1f, 0.5f, 0.5f, 1f);
                else batch.setColor(Color.WHITE);

                batch.draw(characterTexture, drawX, drawY, TILE_SIZE, TILE_SIZE);
                batch.setColor(Color.WHITE);
                font.draw(batch, unit.getCurrentHp() + "/" + unit.getCardData().hp, drawX + 10, drawY + 190);

                if (unit == selectedUnit) {
                    batch.draw(selectionTexture, drawX, drawY, TILE_SIZE, TILE_SIZE);
                }
            }

            // Fantasma da invocação (Visual Aid)
            if (selectedCard != null) {
                gameViewport.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
                batch.setColor(1, 1, 1, 0.5f);
                batch.draw(characterTexture, touchPoint.x - TILE_SIZE/2, touchPoint.y - TILE_SIZE/2, TILE_SIZE, TILE_SIZE);
                batch.setColor(Color.WHITE);
            }
            batch.end();
        }

        // 3. RENDERIZAÇÃO DO HUD (MÃO) - USANDO LISTA ENCADEADA
        hudViewport.apply();
        batch.setProjectionMatrix(hudViewport.getCamera().combined);
        batch.begin();

        MyLinkedList<Card> hand = battleManager.getPlayerHand();
        float startX = 400;
        float spacing = 220;

        // Percorre a lista encadeada para desenhar
        for (int i = 0; i < hand.size(); i++) {
            Card c = hand.get(i); // Acesso customizado

            float cardX = startX + (i * spacing);
            float cardY = (selectedCard == c) ? HAND_Y + 20 : HAND_Y;

            if (selectedCard == c) batch.setColor(Color.YELLOW);
            else batch.setColor(Color.WHITE);

            // Desenha fundo da carta
            batch.draw(cardTexture, cardX, cardY, CARD_WIDTH, CARD_HEIGHT);

            // Desenha textos da carta
            batch.setColor(Color.BLACK);
            cardFont.draw(batch, c.name, cardX + 10, cardY + CARD_HEIGHT - 20);

            if (battleManager.getTurnManager().getCurrentMana() >= c.cost) cardFont.setColor(0, 0, 0.8f, 1);
            else cardFont.setColor(0.8f, 0, 0, 1);

            cardFont.draw(batch, "Mana: " + c.cost, cardX + 10, cardY + CARD_HEIGHT - 60);
            cardFont.setColor(Color.BLACK);
        }
        batch.end();

        // Labels globais
        hudStage.act(delta);
        hudStage.draw();
    }

    private void updateHudInfo() {
        int current = battleManager.getTurnManager().getCurrentMana();
        int max = battleManager.getTurnManager().getMaxMana();
        manaLabel.setText("Mana: " + current + "/" + max);

        if (battleManager.getTurnManager().isPlayerTurn()) {
            turnLabel.setText("Seu Turno (ESPACO p/ passar)");
            turnLabel.setColor(Color.GREEN);
        } else {
            turnLabel.setText("Turno Inimigo");
            turnLabel.setColor(Color.RED);
        }
    }

    @Override
    public void resize(int width, int height) {
        Vector2 scaled = Scaling.fit.apply(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, width, height);
        int viewportWidth = Math.round(scaled.x);
        int viewportHeight = Math.round(scaled.y);
        int viewportX = (width - viewportWidth) / 2;
        int viewportY = (height - viewportHeight) / 2;
        float hudRatio = HUD_HEIGHT / VIRTUAL_HEIGHT;
        int hudScreenHeight = Math.round(viewportHeight * hudRatio);
        int mapScreenHeight = viewportHeight - hudScreenHeight;

        hudViewport.update(viewportWidth, hudScreenHeight, true);
        hudViewport.setScreenBounds(viewportX, viewportY, viewportWidth, hudScreenHeight);
        gameViewport.update(viewportWidth, mapScreenHeight, true);
        gameViewport.setScreenBounds(viewportX, viewportY + hudScreenHeight, viewportWidth, mapScreenHeight);
        gameCamera.position.set(VIRTUAL_WIDTH / 2, MAP_HEIGHT / 2, 0);
        gameCamera.update();
    }

    @Override
    public void dispose() {
        if (map != null) map.dispose();
        if (mapRenderer != null) mapRenderer.dispose();
        if (batch != null) batch.dispose();
        if (characterTexture != null) characterTexture.dispose();
        if (selectionTexture != null) selectionTexture.dispose();
        if (hudStage != null) hudStage.dispose();
        if (deckTexture != null) deckTexture.dispose();
        if (cardTexture != null) cardTexture.dispose();
        if (font != null) font.dispose();
        if (hudFont != null) hudFont.dispose();
        if (cardFont != null) cardFont.dispose();
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
