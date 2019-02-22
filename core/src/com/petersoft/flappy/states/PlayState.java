package com.petersoft.flappy.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.petersoft.flappy.Game;
import com.petersoft.flappy.sprites.Bird;
import com.petersoft.flappy.sprites.Tube;

public class PlayState extends State {

    private Bird bird;
    private Texture bg;
    private Texture ground;
    private Vector2 groundPos1,groundPos2;
    private int score;

    private Array<Tube> tubes;

    private FreeTypeFontGenerator fontGenerator;
    private FreeTypeFontGenerator.FreeTypeFontParameter fontParameter;

    private Vector3 scoreVelocity;
    private Vector3 scorePosition;

    private static final int TUBE_SPACING = 125;
    private static final int TUBE_COUNT =  4;
    private static final int GROUND_OFFEST = -50;
    private static final int MOVEMENT = 100;

    public PlayState(GameStateManager gsm) {
        super(gsm);
        bird = new Bird(50, 300);
        bg = new Texture("bg.png");
        tubes = new Array<Tube>();
        ground = new Texture("ground.png");
        groundPos1 = new Vector2(cam.position.x - cam.viewportWidth / 2, GROUND_OFFEST);
        groundPos2 = new Vector2((cam.position.x - cam.viewportWidth / 2) + ground.getWidth(), GROUND_OFFEST);

        for (int i = 1; i <= TUBE_COUNT; i++) {
            tubes.add(new Tube(i * (TUBE_SPACING + Tube.TUBE_WIDTH)));
        }

        fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("DepredationPixie.ttf"));
        fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        scorePosition = new Vector3(25, 25, 0);
        scoreVelocity = new Vector3(0, 0, 0);

        cam.setToOrtho(false, Game.WIDTH / 2, Game.HEIGHT / 2);
    }

    @Override
    public void handleInput() {

        if (Gdx.input.justTouched()) {
            bird.jump();
        }

    }

    @Override
    public void update(float dt) {
        handleInput();
        bird.update(dt);
        cam.position.x = bird.getPosition().x + 80;

        updateGround();

        for (Tube tube : tubes) {
            if (cam.position.x - (cam.viewportWidth / 2) > tube.getPosTopTube().x + tube.getTopTube().getWidth()) {
                tube.rePosition(tube.getPosTopTube().x + (Tube.TUBE_WIDTH + TUBE_SPACING) * TUBE_COUNT);
                score++;
                System.out.println(score);
            }

            if (tube.collides(bird.getBounds())) {
                gsm.set(new GameOverState(gsm, score));
                break;
            }
        }

        if (bird.getPosition().y <= ground.getHeight() + GROUND_OFFEST) {
            gsm.set(new GameOverState(gsm, score));
        }

        scoreVelocity.scl(dt);
        scorePosition.add(MOVEMENT * dt, scoreVelocity.y, 0);
        scoreVelocity.scl(1/dt);

        cam.update();
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        sb.draw(bg, cam.position.x - (cam.viewportWidth / 2), 0);
        sb.draw(bird.getTexture(), bird.getPosition().x, bird.getPosition().y);

        for (Tube tube : tubes) {
            sb.draw(tube.getTopTube(), tube.getPosTopTube().x, tube.getPosTopTube().y);
            sb.draw(tube.getBottomTube(), tube.getPosBottomTube().x, tube.getPosBottomTube().y);
        }

        sb.draw(ground, groundPos1.x, groundPos1.y);
        sb.draw(ground, groundPos2.x, groundPos2.y);

        BitmapFont font = formatScoreText();
        //font.draw(sb, Integer.toString(score), scorePosition.x, scorePosition.y);

        sb.end();
    }

    @Override
    public void dispose() {
        bg.dispose();
        bird.dispose();
        ground.dispose();
        for (Tube tube : tubes) {
            tube.dispose();
        }
        fontGenerator.dispose();
        System.out.println("Play state disposed.");
    }

    private void updateGround() {
        if (cam.position.x - (cam.viewportWidth / 2) > groundPos1.x + ground.getWidth()) {
            groundPos1.add(ground.getWidth() * 2, 0);
        }

        if (cam.position.x - (cam.viewportWidth / 2) > groundPos2.x + ground.getWidth()) {
            groundPos2.add(ground.getWidth() * 2, 0);
        }
    }

    private BitmapFont formatScoreText() {
        fontParameter.size = 24;
        fontParameter.borderColor.set(Color.DARK_GRAY);
        fontParameter.color.set(Color.RED);
        fontParameter.shadowColor.set(Color.BLACK);
        fontParameter.shadowOffsetX = 1;
        fontParameter.shadowOffsetY = 1;
        BitmapFont scoreFont = fontGenerator.generateFont(fontParameter);

        return scoreFont;
    }
}
