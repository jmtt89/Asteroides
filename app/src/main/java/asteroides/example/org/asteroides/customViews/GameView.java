package asteroides.example.org.asteroides.customViews;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.VectorDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import asteroides.example.org.asteroides.R;
import asteroides.example.org.asteroides.models.base.Grafico;

/**
 * Created by jmtt_ on 10/22/2017.
 * Por los momentos esta es la vista que representa el juego
 */

public class GameView extends View {
    ////// ASTEROIDES //////
    private List<Grafico> asteroides; // Lista con los Asteroides
    private int numAsteroides = 5; // Número inicial de asteroides
    private int numFragmentos = 3; // Fragmentos en que se divide

    ////// NAVE //////
    private Grafico nave; // Gráfico de la nave
    private int giroNave; // Incremento de dirección
    private double aceleracionNave; // aumento de velocidad
    private static final int MAX_VELOCIDAD_NAVE = 20;
    // Incremento estándar de giro y aceleración
    private static final int PASO_GIRO_NAVE = 5;
    private static final float PASO_ACELERACION_NAVE = 0.5f;

    private Drawable drawableNave, drawableAsteroide, drawableMisil;

    private void setupGrafico(Context context, int type){
        switch (type){
            case 0:
                setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                setBackgroundColor(Color.BLACK);

                // SpaceShip
                Path pathShip = new Path();
                pathShip.moveTo((float) 0.0, (float) 0.0);
                pathShip.lineTo((float) 1.0, (float) 0.5);
                pathShip.lineTo((float) 0.0, (float) 1.0);
                pathShip.lineTo((float) 0.0, (float) 0.0);
                pathShip.lineTo((float) -0.2, (float) -0.1);
                pathShip.moveTo((float) 0.0, (float) 1.0);
                pathShip.lineTo((float) -0.2, (float) 1.1);
                ShapeDrawable dShip = new ShapeDrawable(new PathShape(pathShip, 1, 1));
                dShip.getPaint().setColor(Color.WHITE);
                dShip.getPaint().setStyle(Paint.Style.STROKE);
                dShip.setIntrinsicWidth(50);
                dShip.setIntrinsicHeight(50);
                drawableNave = dShip;

                // Asteroids
                Path pathAsteroide = new Path();
                pathAsteroide.moveTo((float) 0.3, (float) 0.0);
                pathAsteroide.lineTo((float) 0.6, (float) 0.0);
                pathAsteroide.lineTo((float) 0.6, (float) 0.3);
                pathAsteroide.lineTo((float) 0.8, (float) 0.2);
                pathAsteroide.lineTo((float) 1.0, (float) 0.4);
                pathAsteroide.lineTo((float) 0.8, (float) 0.6);
                pathAsteroide.lineTo((float) 0.9, (float) 0.9);
                pathAsteroide.lineTo((float) 0.8, (float) 1.0);
                pathAsteroide.lineTo((float) 0.4, (float) 1.0);
                pathAsteroide.lineTo((float) 0.0, (float) 0.6);
                pathAsteroide.lineTo((float) 0.0, (float) 0.2);
                pathAsteroide.lineTo((float) 0.3, (float) 0.0);
                ShapeDrawable dAsteroide = new ShapeDrawable(new PathShape(pathAsteroide, 1, 1));
                dAsteroide.getPaint().setColor(Color.WHITE);
                dAsteroide.getPaint().setStyle(Paint.Style.STROKE);
                dAsteroide.setIntrinsicWidth(50);
                dAsteroide.setIntrinsicHeight(50);
                drawableAsteroide = dAsteroide;
                // Missiles
                break;
            case 2:
                setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                setBackgroundColor(Color.BLACK);

                // SpaceShip
                drawableNave = VectorDrawableCompat.create(getResources(), R.drawable.vector_ship, null);

                // Asteroids
                drawableAsteroide = VectorDrawableCompat.create(getResources(), R.drawable.vector_asteroid_1, null);

                // Missiles
                break;
            case 1:
            case 3:
            default:
                setLayerType(View.LAYER_TYPE_HARDWARE, null);

                // SpaceShip
                drawableNave = context.getResources().getDrawable(R.drawable.nave);
                ContextCompat.getDrawable(context, R.drawable.nave);

                // Asteroids
                drawableAsteroide = context.getResources().getDrawable(R.drawable.asteroide1);
                ContextCompat.getDrawable(context, R.drawable.asteroide1);

                // Missiles
                break;
        }
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        setupGrafico(context, Integer.parseInt(pref.getString("graphicType", "1")));

        asteroides = new ArrayList<Grafico>();
        for (int i = 0; i < numAsteroides; i++) {
            Grafico asteroide = new Grafico(this, drawableAsteroide);
            asteroide.setIncY(Math.random() * 4 - 2);
            asteroide.setIncX(Math.random() * 4 - 2);
            asteroide.setAngulo((int) (Math.random() * 360));
            asteroide.setRotacion((int) (Math.random() * 8 - 4));
            asteroides.add(asteroide);
        }

        nave = new Grafico(this, drawableNave);
    }

    @Override
    protected void onSizeChanged(int ancho, int alto, int ancho_anter, int alto_anter) {
        super.onSizeChanged(ancho, alto, ancho_anter, alto_anter);
        // Una vez que conocemos nuestro ancho y alto.
        for (Grafico asteroide : asteroides) {
            do {
                asteroide.setCenX((int) (Math.random() * ancho));
                asteroide.setCenY((int) (Math.random() * alto));
            } while (asteroide.distancia(nave) < (ancho + alto) / 5);
        }


        nave.setCenX(ancho / 2);
        nave.setCenY(alto / 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Grafico asteroide : asteroides) {
            asteroide.dibujaGrafico(canvas);
        }
        nave.dibujaGrafico(canvas);
    }
}
