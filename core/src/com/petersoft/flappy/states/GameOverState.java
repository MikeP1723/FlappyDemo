package com.petersoft.flappy.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.petersoft.flappy.Game;

public class GameOverState extends State {

    private Texture background;
    private Texture restartButton;
    private int score;
    private Label gameOverLabel;
    private FreeTypeFontGenerator generator;
    private FreeTypeFontGenerator.FreeTypeFontParameter parameter;

    // TODO: Add score, change button, display Game Over graphic

    protected GameOverState(GameStateManager gsm, int score) {

        super(gsm);
        cam.setToOrtho(false, Game.WIDTH / 2, Game.HEIGHT / 2);
        background = new Texture("bg.png");
        restartButton = new Texture("playbtn.png");
        generator = new FreeTypeFontGenerator(Gdx.files.internal("DepredationPixie.ttf"));
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        this.score = score;

    }

    @Override
    protected void handleInput() {

        if (Gdx.input.justTouched()) {
            gsm.set(new PlayState(gsm));
        }

    }

    @Override
    public void update(float dt) {
        handleInput();

    }

    @Override
    public void render(SpriteBatch sb) {

        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        sb.draw(background, 0, 0);
        //sb.draw(restartButton, cam.position.x - restartButton.getWidth() / 2, cam.position.y);

        // Font stuff
        BitmapFont scoreFont = formatScoreText();

        //Label.LabelStyle label1Style = new Label.LabelStyle();
        //label1Style.font = scoreFont;
        //gameOverLabel = new Label("Game Over (BitmapFont)", label1Style);
        //gameOverLabel.setAlignment(Align.center);

        String scoreText = (score <= 9 ? "Score 0" : "Score ") + Integer.toString(score);
        //gameOverLabel.draw(sb, 0);

        scoreFont.draw(sb, scoreText, cam.position.x / 2, cam.position.y + (cam.position.y / 2) + (cam.position.y / 6));
        scoreFont.draw(sb, "Game Over", cam.position.x / 2, cam.position.y + (cam.position.y / 2));

        sb.end();

    }

    @Override
    public void dispose() {

        background.dispose();
        restartButton.dispose();
        generator.dispose();
        System.out.println("Game Over state disposed.");

    }

    private BitmapFont formatScoreText() {
        parameter.size = 24;
        parameter.borderColor.set(Color.DARK_GRAY);
        parameter.color.set(Color.RED);
        parameter.shadowColor.set(Color.BLACK);
        parameter.shadowOffsetX = 1;
        parameter.shadowOffsetY = 1;
        BitmapFont scoreFont = generator.generateFont(parameter);

        return scoreFont;
    }
}
