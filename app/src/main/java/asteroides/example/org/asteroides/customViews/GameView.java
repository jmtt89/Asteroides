package asteroides.example.org.asteroides.customViews;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.graphics.drawable.shapes.RectShape;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import asteroides.example.org.asteroides.R;
import asteroides.example.org.asteroides.models.base.Grafico;
import asteroides.example.org.asteroides.views.gameBoard.FinishListener;
import asteroides.example.org.asteroides.views.gameBoard.MissileListener;
import asteroides.example.org.asteroides.views.gameBoard.ScoreUpdateListener;

/**
 * Created by jmtt_ on 10/22/2017.
 * Por los momentos esta es la vista que representa el juego
 */

public class GameView extends View implements SensorEventListener {
    private boolean musicEnable;
    private MissileListener missileListener;
    private ScoreUpdateListener scoreUpdateListener;
    private FinishListener finishListener;

    public void setMissileListener(MissileListener missileListener) {
        this.missileListener = missileListener;
    }

    public void setScoreUpdateListener(ScoreUpdateListener scoreUpdateListener) {
        this.scoreUpdateListener = scoreUpdateListener;
    }

    public void setFinishListener(FinishListener finishListener) {
        this.finishListener = finishListener;
    }

    public class ThreadJuego extends Thread {
        private boolean pausa, corriendo;

        public synchronized void pausar() {
            pausa = true;
            notify();
        }

        public synchronized void reanudar() {
            pausa = false;
            ultimoProceso =  System.currentTimeMillis();
            notify();
        }

        public void detener() {
            corriendo = false;
            if (pausa) {
                reanudar();
            }
        }

        @Override
        public void run() {
            corriendo = true;
            while (corriendo) {
                actualizaFisica();
                synchronized (this) {
                    while (pausa) {
                        try {
                            wait();
                        } catch (Exception ignored) {}
                    }
                }
            }
        }
    }

    // //// THREAD Y TIEMPO //////
    // Thread encargado de procesar el juego
    private ThreadJuego thread = new ThreadJuego();
    // Cada cuanto queremos procesar cambios (ms)
    private static int PERIODO_PROCESO = 50;
    // Cuando se realizó el último proceso
    private long ultimoProceso = 0;


    ////// ASTEROIDES //////
    private List<Grafico> asteroides; // Lista con los Asteroides
    private int numAsteroides = 5; // Número inicial de asteroides
    private int numFragmentos = 3; // Fragmentos en que se divide

    ////// NAVE //////
    private Grafico nave; // Gráfico de la nave
    private int giroNave; // Incremento de dirección
    private double aceleracionNave; // aumento de velocidad
    private static final int MAX_VELOCIDAD_NAVE = 20;

    ////// MISIL //////
    private static int PASO_VELOCIDAD_MISIL = 12;
    private Hashtable<Grafico, Integer> misiles;
    //En luegar de utilizar dos vectores diferentes es preferible utilizar un hashmap
    //private Vector<Grafico> misiles;
    //private Vector<Integer> tiempoMisiles;

    // Incremento estándar de giro y aceleración
    private static final int PASO_GIRO_NAVE = 5;
    private static final float PASO_ACELERACION_NAVE = 0.5f;

    ////// MULTIMEDIA //////
    SoundPool soundPool;
    int idDisparo, idExplosion;

    //
    private Set<String> validInputs;
    private boolean limitMissiles;

    //Touch
    private float mX = 0, mY = 0;
    private boolean disparo = false;

    //Sensors
    private boolean hayValorInicial = false;
    private float valorInicial;

    private boolean hayValorInicialA = false;
    private float valorInicialA;


    private Drawable drawableNave, drawableAsteroide, drawableMisil;

    private void setupGrafico(Context context, int type) {
        switch (type) {
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
                ShapeDrawable dMisil = new ShapeDrawable(new RectShape());
                dMisil.getPaint().setColor(Color.WHITE);
                dMisil.getPaint().setStyle(Paint.Style.STROKE);
                dMisil.setIntrinsicWidth(15);
                dMisil.setIntrinsicHeight(3);
                drawableMisil = dMisil;
                break;
            case 2:
                setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                setBackgroundColor(Color.BLACK);

                // SpaceShip
                drawableNave = VectorDrawableCompat.create(getResources(), R.drawable.vector_ship, null);

                // Asteroids
                drawableAsteroide = VectorDrawableCompat.create(getResources(), R.drawable.vector_asteroid_1, null);

                // Missiles
                drawableMisil = VectorDrawableCompat.create(getResources(), R.drawable.vector_blast, null);
                break;
            case 1:
            case 3:
            default:
                setLayerType(View.LAYER_TYPE_HARDWARE, null);

                // SpaceShip
                drawableNave = ContextCompat.getDrawable(context, R.drawable.nave);

                // Asteroids
                drawableAsteroide = ContextCompat.getDrawable(context, R.drawable.asteroide1);

                // Missiles
                ImageView tmp = new ImageView(getContext());
                tmp.setBackgroundResource(R.drawable.misil_animated);
                drawableMisil = tmp.getBackground();
                //drawableMisil = ContextCompat.getDrawable(context, R.drawable.misil_animated);
                break;
        }
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());

        musicEnable = pref.getBoolean("hasMusic", true);
        validInputs = pref.getStringSet("validInputs", null);
        limitMissiles = pref.getBoolean("limitMissiles", true);
        numFragmentos = Integer.parseInt(pref.getString("fragments", "3"));

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
        misiles = new Hashtable<>();
        //tiempoMisiles = new Vector<>();
        //misiles = new Vector<>();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes  att = new AudioAttributes.Builder().setLegacyStreamType(AudioManager.STREAM_MUSIC).build();
            soundPool = new SoundPool.Builder().setMaxStreams(5).setAudioAttributes(att).build();
        }else{
            soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        }
        idDisparo = soundPool.load(context, R.raw.disparo, 1);
        idExplosion = soundPool.load(context, R.raw.explosion, 1);
    }

    public ThreadJuego getThread() {
        return thread;
    }

    public void activarSensores(Context context) {
        SensorManager mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> listSensors = mSensorManager.getSensorList(Sensor.TYPE_ORIENTATION);
        if (!listSensors.isEmpty()) {
            Sensor orientationSensor = listSensors.get(0);
            mSensorManager.registerListener(this, orientationSensor, SensorManager.SENSOR_DELAY_GAME);
        }

        listSensors = mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (!listSensors.isEmpty()) {
            Sensor acelerometerSensor = listSensors.get(0);
            mSensorManager.registerListener(this, acelerometerSensor, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    public void desactivarSensores(Context context) {
        SensorManager mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (validInputs.contains("2")) {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ORIENTATION:
                    /*
                    float valor = event.values[1];
                    if (!hayValorInicial) {
                        valorInicial = valor;
                        hayValorInicial = true;
                    }
                    giroNave = (int) (valor - valorInicial) / 3;
                    */
                    break;
                case Sensor.TYPE_ACCELEROMETER:

                    float valor = event.values[1];
                    if (!hayValorInicial) {
                        valorInicial = valor;
                        hayValorInicial = true;
                    }
                    giroNave = (int) (valor - valorInicial) / 2;

                    float valorA = event.values[2];
                    if (!hayValorInicialA) {
                        valorInicialA = valorA;
                        hayValorInicialA = true;
                    }
                    aceleracionNave = (valorA - valorInicialA) / 28;
                    break;
            }
        }
    }

    @Override
    public boolean onKeyDown(int codigoTecla, KeyEvent evento) {
        super.onKeyDown(codigoTecla, evento);
        // Procesamos la pulsasion si estamos keyboard esta habilitado
        boolean procesada = validInputs.contains("0");
        if (procesada) {
            switch (codigoTecla) {
                case KeyEvent.KEYCODE_DPAD_UP:
                    aceleracionNave = +PASO_ACELERACION_NAVE;
                    break;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    giroNave = -PASO_GIRO_NAVE;
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    giroNave = +PASO_GIRO_NAVE;
                    break;
                case KeyEvent.KEYCODE_DPAD_CENTER:
                case KeyEvent.KEYCODE_ENTER:
                    if(!limitMissiles || misiles.size()<3){
                        activaMisil();
                    }
                    break;
                default:
                    // Si estamos aquí, no hay pulsación que nos interese
                    procesada = false;
                    break;
            }
        }
        return procesada;
    }

    @Override
    public boolean onKeyUp(int codigoTecla, KeyEvent evento) {
        super.onKeyUp(codigoTecla, evento);
        // Procesamos la pulsasion si estamos keyboard esta habilitado
        boolean procesada = validInputs.contains("0");
        if (procesada) {
            switch (codigoTecla) {
                case KeyEvent.KEYCODE_DPAD_UP:
                    aceleracionNave = 0;
                    break;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    giroNave = 0;
                    break;
                default:
                    // Si estamos aquí, no hay pulsación que nos interese
                    procesada = false;
                    break;
            }
        }
        return procesada;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                disparo = true;
                break;
            case MotionEvent.ACTION_MOVE:
                if (validInputs.contains("1")) {
                    float dx = Math.abs(x - mX);
                    float dy = Math.abs(y - mY);
                    if (dy < 6 && dx > 6) {
                        giroNave = Math.round((x - mX) / 2);
                        disparo = false;
                    } else if (dx < 6 && dy > 6) {
                        aceleracionNave = Math.round(dy / 25);
                        disparo = false;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                giroNave = 0;
                aceleracionNave = 0;
                if (disparo) {
                    if(!limitMissiles || misiles.size()<3){
                        activaMisil();
                    }
                }
                break;
        }
        mX = x;
        mY = y;
        return disparo || validInputs.contains("1");
    }

    @Override
    protected void onSizeChanged(int ancho, int alto, int ancho_anter, int alto_anter) {
        super.onSizeChanged(ancho, alto, ancho_anter, alto_anter);
        nave.setCenX(ancho / 2);
        nave.setCenY(alto / 2);

        // Una vez que conocemos nuestro ancho y alto.
        for (Grafico asteroide : asteroides) {
            do {
                asteroide.setCenX((int) (Math.random() * ancho));
                asteroide.setCenY((int) (Math.random() * alto));
            } while (asteroide.distancia(nave) < (ancho + alto) / 5);
        }

        ultimoProceso = System.currentTimeMillis();
        if(!thread.isAlive()){
            thread.start();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        synchronized (asteroides) {
            for (Grafico asteroide : asteroides) {
                asteroide.dibujaGrafico(canvas);
            }
        }

        synchronized (misiles) {
            for (Map.Entry<Grafico, Integer> misil : misiles.entrySet()) {
                if (misil.getKey().getDrawable() instanceof AnimationDrawable && !((AnimationDrawable) misil.getKey().getDrawable()).isRunning()) {
                    ((AnimationDrawable) misil.getKey().getDrawable()).start();
                }
                misil.getKey().dibujaGrafico(canvas);
            }
        }

        nave.dibujaGrafico(canvas);
    }

    protected void actualizaFisica() {
        long ahora = System.currentTimeMillis();
        if (ultimoProceso + PERIODO_PROCESO > ahora) {
            return;    // Salir si el período de proceso no se ha cumplido.
        }
        // Para una ejecución en tiempo real calculamos el factor de movimiento
        double factorMov = (ahora - ultimoProceso) / PERIODO_PROCESO;
        ultimoProceso = ahora; // Para la próxima vez
        // Actualizamos velocidad y dirección de la nave a partir de
        // giroNave y aceleracionNave (según la entrada del jugador)
        nave.setAngulo((int) (nave.getAngulo() + giroNave * factorMov));
        double nIncX = nave.getIncX() + aceleracionNave * Math.cos(Math.toRadians(nave.getAngulo())) * factorMov;
        double nIncY = nave.getIncY() + aceleracionNave * Math.sin(Math.toRadians(nave.getAngulo())) * factorMov;
        // Actualizamos si el módulo de la velocidad no excede el máximo
        if (Math.hypot(nIncX, nIncY) <= MAX_VELOCIDAD_NAVE) {
            nave.setIncX(nIncX);
            nave.setIncY(nIncY);
        }
        nave.incrementaPos(factorMov); // Actualizamos posición

        for (Grafico asteroide : asteroides) {
            asteroide.incrementaPos(factorMov);
        }

        // Actualizamos posición de misil

        synchronized (misiles) {
            List<Grafico> toDelete = new ArrayList<>();
            for (Map.Entry<Grafico, Integer> misil : misiles.entrySet()) {
                misil.getKey().incrementaPos(factorMov);
                int dM = (int) (misil.getValue() - factorMov);
                if (dM < 0) {
                    toDelete.add(misil.getKey());
                } else {
                    misiles.put(misil.getKey(), dM);
                    for (int j = 0; j < asteroides.size(); j++)
                        if (misil.getKey().verificaColision(asteroides.get(j))) {
                            destruyeAsteroide(j);
                            toDelete.add(misil.getKey());
                            break;
                        }
                }
            }
            for (Grafico index : toDelete) {
                misiles.remove(index);
                missileListener.dismissMissile();
            }
        }
    }

    private void destruyeAsteroide(int i) {
        if(musicEnable){
            soundPool.play(idExplosion, 1, 1, 8, 0, 1.0f);
        }
        synchronized (asteroides) {
            asteroides.remove(i);
            scoreUpdateListener.scoreUpdate(100);
            if(asteroides.size() == 0){
                finishListener.wonMessage();
            }
        }
        this.postInvalidate();
    }

    private void activaMisil() {
        final Grafico misil = new Grafico(this, drawableMisil);
        misil.setCenX(nave.getCenX());
        misil.setCenY(nave.getCenY());
        misil.setAngulo(nave.getAngulo());
        misil.setIncX(Math.cos(Math.toRadians(misil.getAngulo())) * PASO_VELOCIDAD_MISIL);
        misil.setIncY(Math.sin(Math.toRadians(misil.getAngulo())) * PASO_VELOCIDAD_MISIL);
        misiles.put(misil, (int) Math.min(this.getWidth() / Math.abs(misil.getIncX()), this.getHeight() / Math.abs(misil.getIncY())) - 2);
        missileListener.fireMissile();
        if(musicEnable){
            soundPool.play(idDisparo, 1, 1, 10, 0, 1.0f);
        }
        /*
        drawableMisil.setCallback(new Drawable.Callback() {

            @Override
            public void unscheduleDrawable(Drawable who, Runnable what) {
                misil.getView().removeCallbacks(what);
            }

            @Override
            public void scheduleDrawable(Drawable who, Runnable what, long when) {
                misil.getView().postDelayed(what, when - SystemClock.uptimeMillis());
            }

            @Override
            public void invalidateDrawable(Drawable who) {
                misil.getView().postInvalidate();
            }
        });
        */
    }

}
